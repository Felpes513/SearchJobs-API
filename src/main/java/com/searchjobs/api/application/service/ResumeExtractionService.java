package com.searchjobs.api.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchjobs.api.application.dto.internal.ParsedResumeDto;
import com.searchjobs.api.application.dto.response.ResumeExtractionResponse;
import com.searchjobs.api.domain.exception.ResumeNotFoundException;
import com.searchjobs.api.domain.model.*;
import com.searchjobs.api.domain.exception.MissingApiKeyException;
import com.searchjobs.api.domain.port.in.JobMatchUseCase;
import com.searchjobs.api.domain.port.in.JobSearchUseCase;
import com.searchjobs.api.domain.port.in.ResumeExtractionUseCase;
import com.searchjobs.api.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeExtractionService implements ResumeExtractionUseCase {

    private final ResumeRepository resumeRepository;
    private final AiExtractionPort aiExtractionPort;
    private final UserProfileRepository userProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserExperienceRepository userExperienceRepository;
    private final UserCertificationRepository userCertificationRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final JobSearchUseCase jobSearchUseCase;
    private final JobMatchUseCase jobMatchUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public ResumeExtractionResponse extract(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        String openAiKey = userSettingsRepository.findByUserId(resume.getUserId())
                .map(s -> s.getOpenAiApiKey())
                .filter(k -> k != null && !k.isBlank())
                .orElseThrow(() -> new MissingApiKeyException(
                        "Configure sua OpenAI API Key nas configurações antes de extrair o currículo"));

        String extractedText = extractTextFromPdf(resume.getFilePath());
        String prompt = buildExtractionPrompt(extractedText);
        String parsedJson = aiExtractionPort.extractResumeData(prompt, openAiKey);

        Resume updated = Resume.builder()
                .id(resume.getId())
                .userId(resume.getUserId())
                .fileName(resume.getFileName())
                .filePath(resume.getFilePath())
                .extractedText(extractedText)
                .parsedJson(parsedJson)
                .createdAt(resume.getCreatedAt())
                .build();

        resumeRepository.update(updated);

        ParsedResumeDto parsed = parseJson(parsedJson);
        List<String> camposFaltando = populateProfile(resume.getUserId(), parsed);

        jobSearchUseCase.evictJobsCache(resume.getUserId());
        jobMatchUseCase.evictMatchAllCache(resume.getUserId());

        return ResumeExtractionResponse.builder()
                .resumeId(resumeId)
                .mensagem("Currículo extraído e perfil atualizado com sucesso")
                .camposFaltando(camposFaltando)
                .build();
    }

    private List<String> populateProfile(Long userId, ParsedResumeDto parsed) {
        List<String> faltando = new ArrayList<>();

        userProfileRepository.deleteByUserId(userId);
        userSkillRepository.deleteByUserId(userId);
        userExperienceRepository.deleteByUserId(userId);
        userCertificationRepository.deleteByUserId(userId);
        userProjectRepository.deleteByUserId(userId);

        userProfileRepository.save(UserProfile.builder()
                .userId(userId)
                .resumoProfissional(parsed.getResumoProfissional())
                .cargoDesejado(parsed.getCargoDesejado())
                .cidade(parsed.getCidade())
                .estado(parsed.getEstado())
                .linkedinUrl(parsed.getLinkedinUrl())
                .githubUrl(parsed.getGithubUrl())
                .build());

        if (isBlank(parsed.getNome()))     faltando.add("nome");
        if (isBlank(parsed.getEmail()))    faltando.add("email");
        if (isBlank(parsed.getTelefone())) faltando.add("telefone");

        if (parsed.getSkills() == null || parsed.getSkills().isEmpty()) {
            faltando.add("skills");
        } else {
            userSkillRepository.saveAll(userId, parsed.getSkills());
        }

        if (parsed.getExperiencias() == null || parsed.getExperiencias().isEmpty()) {
            faltando.add("experiencias");
        } else {
            List<UserExperience> experiences = parsed.getExperiencias().stream()
                    .map(e -> UserExperience.builder()
                            .userId(userId)
                            .cargo(e.getCargo())
                            .empresa(e.getEmpresa())
                            .descricao(e.getDescricao())
                            .dataInicio(e.getDataInicio())
                            .dataFim(e.getDataFim())
                            .build())
                    .toList();
            userExperienceRepository.saveAll(experiences);
        }

        if (parsed.getCertificacoes() == null || parsed.getCertificacoes().isEmpty()) {
            faltando.add("certificacoes");
        } else {
            List<UserCertification> certifications = parsed.getCertificacoes().stream()
                    .map(c -> UserCertification.builder()
                            .userId(userId)
                            .nomeCertificacao(c.getNome())
                            .instituicao(c.getInstituicao())
                            .dataObtencao(c.getDataObtencao())
                            .build())
                    .toList();
            userCertificationRepository.saveAll(certifications);
        }

        if (parsed.getProjetos() == null || parsed.getProjetos().isEmpty()) {
            faltando.add("projetos");
        } else {
            List<UserProject> projects = parsed.getProjetos().stream()
                    .map(p -> UserProject.builder()
                            .userId(userId)
                            .nome(p.getNome())
                            .descricao(p.getDescricao())
                            .stack(p.getStack())
                            .link(p.getLink())
                            .build())
                    .toList();
            userProjectRepository.saveAll(projects);
        }

        return faltando;
    }

    private ParsedResumeDto parseJson(String json) {
        try {
            return objectMapper.readValue(json, ParsedResumeDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao interpretar resposta do GPT: " + e.getMessage());
        }
    }

    private String extractTextFromPdf(String filePath) {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao extrair texto do PDF: " + e.getMessage());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String buildExtractionPrompt(String resumeText) {
        return """
            Você é um extrator de informações de currículos. Analise o texto abaixo e retorne APENAS um JSON válido, sem markdown, sem explicações, somente o JSON.

            O JSON deve ter exatamente esta estrutura:
            {
              "nome": "string",
              "email": "string",
              "telefone": "string",
              "cidade": "string",
              "estado": "string",
              "linkedinUrl": "string",
              "githubUrl": "string",
              "resumoProfissional": "string",
              "cargoDesejado": "string",
              "skills": ["string"],
              "experiencias": [
                {
                  "cargo": "string",
                  "empresa": "string",
                  "descricao": "string",
                  "dataInicio": "string",
                  "dataFim": "string"
                }
              ],
              "certificacoes": [
                {
                  "nome": "string",
                  "instituicao": "string",
                  "dataObtencao": "string"
                }
              ],
              "projetos": [
                {
                  "nome": "string",
                  "descricao": "string",
                  "stack": "string",
                  "link": "string"
                }
              ]
            }

            REGRAS IMPORTANTES:
            - "resumoProfissional": extraia o objetivo ou resumo profissional do candidato
            - "cidade" e "estado": extraia do endereço presente no currículo
            - "linkedinUrl": extraia a URL completa do LinkedIn se presente
            - "githubUrl": extraia o usuário ou URL do GitHub se presente
            - "skills": extraia APENAS tecnologias, linguagens, frameworks e ferramentas específicas. Exemplos corretos: "Java", "Spring Boot", "Angular", "MySQL", "Docker", "Cypress". Exemplos INCORRETOS: "Desenvolvimento Full Stack", "Qualidade de Software"
            - "certificacoes": inclua também formações acadêmicas como certificações, com instituição e data de conclusão
            - "experiencias" - datas: converta para formato YYYY-MM se possível, caso contrário mantenha o texto original
            - Para campos não encontrados use null para strings e [] para arrays
            - "cargoDesejado": extraia o cargo ou objetivo profissional do candidato. Exemplos: "Desenvolvedor Java Junior", "Analista de Testes", "Desenvolvedor Full Stack". Deve ser curto, no máximo 5 palavras.
            - "resumoProfissional": extraia o texto completo do objetivo ou resumo profissional

            Texto do currículo:
            %s
            """.formatted(resumeText);
    }
}