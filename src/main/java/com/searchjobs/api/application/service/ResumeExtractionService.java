package com.searchjobs.api.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchjobs.api.application.dto.internal.ParsedResumeDto;
import com.searchjobs.api.application.dto.response.ResumeExtractionResponse;
import com.searchjobs.api.domain.exception.ResumeNotFoundException;
import com.searchjobs.api.domain.model.*;
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
    private final ObjectMapper objectMapper;

    @Override
    public ResumeExtractionResponse extract(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        String extractedText = extractTextFromPdf(resume.getFilePath());
        String parsedJson = aiExtractionPort.extractResumeData(extractedText);

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

        return ResumeExtractionResponse.builder()
                .resumeId(resumeId)
                .mensagem("Currículo extraído e perfil atualizado com sucesso")
                .camposFaltando(camposFaltando)
                .build();
    }

    private List<String> populateProfile(Long userId, ParsedResumeDto parsed) {
        List<String> faltando = new ArrayList<>();

        // Limpa dados anteriores
        userProfileRepository.deleteByUserId(userId);
        userSkillRepository.deleteByUserId(userId);
        userExperienceRepository.deleteByUserId(userId);
        userCertificationRepository.deleteByUserId(userId);
        userProjectRepository.deleteByUserId(userId);

        // Salva perfil base
        userProfileRepository.save(UserProfile.builder()
                .userId(userId)
                .build());

        // Verifica campos faltando
        if (isBlank(parsed.getNome()))         faltando.add("nome");
        if (isBlank(parsed.getEmail()))        faltando.add("email");
        if (isBlank(parsed.getTelefone()))     faltando.add("telefone");

        // Skills
        if (parsed.getSkills() == null || parsed.getSkills().isEmpty()) {
            faltando.add("skills");
        } else {
            userSkillRepository.saveAll(userId, parsed.getSkills());
        }

        // Experiências
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

        // Certificações
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

        // Projetos
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
}