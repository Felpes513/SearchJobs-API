package com.searchjobs.api.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchjobs.api.application.dto.internal.JobMatchResultDto;
import com.searchjobs.api.application.dto.response.JobMatchResponse;
import com.searchjobs.api.domain.model.*;
import com.searchjobs.api.domain.port.in.JobMatchUseCase;
import com.searchjobs.api.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobMatchService implements JobMatchUseCase {

    private final JobMatchRepository jobMatchRepository;
    private final JobRepository jobRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserExperienceRepository userExperienceRepository;
    private final UserProjectRepository userProjectRepository;
    private final AiExtractionPort aiExtractionPort;
    private final ObjectMapper objectMapper;

    @Override
    public List<JobMatchResponse> matchAll(Long userId) {
        List<Job> allJobs = jobRepository.findAll();

        if (allJobs.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhuma vaga encontrada. Faça uma busca de vagas primeiro."
            );
        }

        List<String> skills = userSkillRepository.findAllByUserId(userId)
                .stream()
                .map(UserSkill::getNomeSkill)
                .toList();

        List<Job> filteredJobs = preFilter(allJobs, skills);
        String perfilTexto = buildPerfilTexto(userId);

        return filteredJobs.stream()
                .map(job -> {
                    jobMatchRepository.deleteByUserIdAndJobId(userId, job.getId());
                    JobMatchResultDto result = analyzeMatch(perfilTexto, job);
                    JobMatch match = saveMatch(userId, job.getId(), result);
                    return toResponse(match, job, result);
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .toList();
    }

    @Override
    public List<JobMatchResponse> getMatches(Long userId) {
        return jobMatchRepository.findAllByUserId(userId)
                .stream()
                .map(match -> {
                    Job job = jobRepository.findById(match.getJobId()).orElse(null);
                    if (job == null) return null;
                    return JobMatchResponse.builder()
                            .jobId(job.getId())
                            .titulo(job.getTitulo())
                            .empresa(job.getEmpresa())
                            .localizacao(job.getLocalizacao())
                            .modeloTrabalho(job.getModeloTrabalho())
                            .jobUrl(job.getJobUrl())
                            .score(match.getScore())
                            .justificativa(match.getJustificativa())
                            .pontosFortres(parseJsonList(match.getPontosFortesJson()))
                            .gaps(parseJsonList(match.getGapsJson()))
                            .build();
                })
                .filter(m -> m != null)
                .toList();
    }

    private List<Job> preFilter(List<Job> jobs, List<String> skills) {
        if (skills.isEmpty()) {
            return jobs.stream().limit(15).toList();
        }

        List<String> skillsLower = skills.stream()
                .map(String::toLowerCase)
                .toList();

        List<Job> comMatch = jobs.stream()
                .map(job -> {
                    String texto = buildJobTexto(job).toLowerCase();
                    long matches = skillsLower.stream()
                            .filter(texto::contains)
                            .count();
                    return new AbstractMap.SimpleEntry<>(job, matches);
                })
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(15)
                .map(AbstractMap.SimpleEntry::getKey)
                .toList();

        if (comMatch.isEmpty()) {
            return jobs.stream().limit(15).toList();
        }

        return comMatch;
    }

    private String buildJobTexto(Job job) {
        StringBuilder sb = new StringBuilder();
        if (job.getTitulo() != null)     sb.append(job.getTitulo()).append(" ");
        if (job.getDescricao() != null)  sb.append(job.getDescricao()).append(" ");
        if (job.getRequisitos() != null) sb.append(job.getRequisitos());
        return sb.toString();
    }

    private String buildPerfilTexto(Long userId) {
        StringBuilder sb = new StringBuilder();

        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            if (profile.getCargoDesejado() != null)
                sb.append("Cargo desejado: ").append(profile.getCargoDesejado()).append("\n");
            if (profile.getResumoProfissional() != null)
                sb.append("Resumo: ").append(profile.getResumoProfissional()).append("\n");
        });

        List<String> skills = userSkillRepository.findAllByUserId(userId)
                .stream().map(UserSkill::getNomeSkill).toList();
        if (!skills.isEmpty())
            sb.append("Skills: ").append(String.join(", ", skills)).append("\n");

        List<UserExperience> experiences = userExperienceRepository.findAllByUserId(userId);
        if (!experiences.isEmpty()) {
            sb.append("Experiências:\n");
            experiences.forEach(e -> sb.append("- ")
                    .append(e.getCargo()).append(" na ").append(e.getEmpresa())
                    .append(": ").append(e.getDescricao()).append("\n"));
        }

        List<UserProject> projects = userProjectRepository.findAllByUserId(userId);
        if (!projects.isEmpty()) {
            sb.append("Projetos:\n");
            projects.forEach(p -> sb.append("- ")
                    .append(p.getNome()).append(" (").append(p.getStack()).append(")\n"));
        }

        return sb.toString();
    }

    private JobMatchResultDto analyzeMatch(String perfilTexto, Job job) {
        String prompt = """
                Você é um especialista em recrutamento tech. Analise a compatibilidade entre o candidato e a vaga abaixo.
                
                PERFIL DO CANDIDATO:
                %s
                
                VAGA:
                Título: %s
                Empresa: %s
                Descrição: %s
                
                Retorne APENAS um JSON válido sem markdown com esta estrutura:
                {
                  "score": número de 0 a 100,
                  "justificativa": "texto explicando o score em 2-3 frases",
                  "pontosFortres": ["skill1", "skill2"],
                  "gaps": ["gap1", "gap2"]
                }
                
                Critérios para o score:
                - 80-100: candidato atende a maioria dos requisitos
                - 60-79: candidato atende requisitos básicos com alguns gaps
                - 40-59: candidato tem base mas falta experiência relevante
                - 0-39: pouca compatibilidade
                """.formatted(
                perfilTexto,
                job.getTitulo(),
                job.getEmpresa(),
                job.getDescricao() != null
                        ? job.getDescricao().substring(0, Math.min(job.getDescricao().length(), 500))
                        : "Não informado"
        );

        String response = aiExtractionPort.extractResumeData(prompt)
                .trim()
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        try {
            return objectMapper.readValue(response, JobMatchResultDto.class);
        } catch (Exception e) {
            System.out.println(">>> Erro ao parsear resposta do GPT: " + e.getMessage());
            System.out.println(">>> Resposta recebida: " + response);
            return JobMatchResultDto.builder()
                    .score(0.0)
                    .justificativa("Não foi possível analisar esta vaga.")
                    .pontosFortres(List.of())
                    .gaps(List.of())
                    .build();
        }
    }

    private JobMatch saveMatch(Long userId, Long jobId, JobMatchResultDto result) {
        try {
            double score = result.getScore() != null ? result.getScore() : 0.0;
            String justificativa = result.getJustificativa() != null
                    ? result.getJustificativa() : "Análise indisponível";
            String pontosFortesJson = objectMapper.writeValueAsString(
                    result.getPontosFortres() != null ? result.getPontosFortres() : List.of());
            String gapsJson = objectMapper.writeValueAsString(
                    result.getGaps() != null ? result.getGaps() : List.of());

            return jobMatchRepository.save(JobMatch.builder()
                    .userId(userId)
                    .jobId(jobId)
                    .score(score)
                    .justificativa(justificativa)
                    .pontosFortesJson(pontosFortesJson)
                    .gapsJson(gapsJson)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar match: " + e.getMessage());
        }
    }

    private JobMatchResponse toResponse(JobMatch match, Job job, JobMatchResultDto result) {
        return JobMatchResponse.builder()
                .jobId(job.getId())
                .titulo(job.getTitulo())
                .empresa(job.getEmpresa())
                .localizacao(job.getLocalizacao())
                .modeloTrabalho(job.getModeloTrabalho())
                .jobUrl(job.getJobUrl())
                .score(match.getScore())
                .justificativa(result.getJustificativa())
                .pontosFortres(result.getPontosFortres())
                .gaps(result.getGaps())
                .build();
    }

    private List<String> parseJsonList(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}