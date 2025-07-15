package com.bika.document.service;

import com.bika.company.entity.Company;
import com.bika.company.repository.CompanyRepository;
import com.bika.document.dto.DocumentFieldDTO;
import com.bika.document.dto.DocumentTypeDTO;
import com.bika.document.entity.DocumentField;
import com.bika.document.entity.DocumentType;
import com.bika.document.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;
    private final CompanyRepository companyRepository;
    private final DocumentFieldService documentFieldService;

    @Transactional(readOnly = true)
    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DocumentType> findById(Long id) {
        return documentTypeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DocumentType> findByCompany(Company company) {
        return documentTypeRepository.findByCompany(company);
    }

    @Transactional(readOnly = true)
    public Optional<DocumentType> findByCompanyAndCode(Company company, String code) {
        return documentTypeRepository.findByCompanyAndCode(company, code);
    }

    @Transactional
    public DocumentType save(DocumentType documentType) {
        return documentTypeRepository.save(documentType);
    }

    @Transactional
    public void deleteById(Long id) {
        documentTypeRepository.deleteById(id);
    }

    // DTO-based methods
    @Transactional(readOnly = true)
    public List<DocumentTypeDTO> getAllDocumentTypesDTO() {
        return findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentTypeDTO> getDocumentTypesByCompanyDTO(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return findByCompany(company).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DocumentTypeDTO> getDocumentTypeDTOById(Long id) {
        return findById(id).map(this::toDTO);
    }

    @Transactional
    public DocumentTypeDTO createDocumentType(DocumentTypeDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        DocumentType documentType = DocumentType.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .company(company)
                .active(dto.getIsActive() != null ? dto.getIsActive() : true)
                .createdBy("system") // TODO: Get from security context
                .updatedBy("system")
                .build();

        DocumentType saved = save(documentType);

        // Save fields if provided
        if (dto.getFields() != null && !dto.getFields().isEmpty()) {
            for (DocumentFieldDTO fieldDTO : dto.getFields()) {
                DocumentField field = DocumentField.builder()
                        .documentType(saved)
                        .name(fieldDTO.getName())
                        .fieldKey(fieldDTO.getFieldKey())
                        .fieldType(fieldDTO.getFieldType())
                        .required(fieldDTO.isRequired())
                        .description(fieldDTO.getDescription())
                        .defaultValue(fieldDTO.getDefaultValue())
                        .validationRules(fieldDTO.getValidationRules())
                        .options(fieldDTO.getOptions())
                        .displayOrder(fieldDTO.getDisplayOrder())
                        .active(fieldDTO.isActive())
                        .createdBy("system")
                        .updatedBy("system")
                        .build();

                documentFieldService.save(field);
            }
        }

        return toDTO(findById(saved.getId()).orElse(saved));
    }

    @Transactional
    public DocumentTypeDTO updateDocumentType(Long id, DocumentTypeDTO dto) {
        DocumentType existingDocumentType = findById(id)
                .orElseThrow(() -> new RuntimeException("Document type not found"));

        existingDocumentType.setName(dto.getName());
        existingDocumentType.setCode(dto.getCode());
        existingDocumentType.setDescription(dto.getDescription());
        existingDocumentType.setActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        existingDocumentType.setUpdatedBy("system"); // TODO: Get from security context

        DocumentType updated = save(existingDocumentType);

        // Update fields - for simplicity, delete existing and recreate
        List<DocumentField> existingFields = documentFieldService.findByDocumentType(updated);
        for (DocumentField field : existingFields) {
            documentFieldService.delete(field);
        }

        // Save new fields if provided
        if (dto.getFields() != null && !dto.getFields().isEmpty()) {
            for (DocumentFieldDTO fieldDTO : dto.getFields()) {
                DocumentField field = DocumentField.builder()
                        .documentType(updated)
                        .name(fieldDTO.getName())
                        .fieldKey(fieldDTO.getFieldKey())
                        .fieldType(fieldDTO.getFieldType())
                        .required(fieldDTO.isRequired())
                        .description(fieldDTO.getDescription())
                        .defaultValue(fieldDTO.getDefaultValue())
                        .validationRules(fieldDTO.getValidationRules())
                        .options(fieldDTO.getOptions())
                        .displayOrder(fieldDTO.getDisplayOrder())
                        .active(fieldDTO.isActive())
                        .createdBy("system")
                        .updatedBy("system")
                        .build();

                documentFieldService.save(field);
            }
        }

        return toDTO(findById(updated.getId()).orElse(updated));
    }

    // Mapper methods
    public DocumentTypeDTO toDTO(DocumentType documentType) {
        List<DocumentFieldDTO> fieldDTOs = documentFieldService.findByDocumentType(documentType)
                .stream()
                .map(this::fieldToDTO)
                .collect(Collectors.toList());

        return DocumentTypeDTO.builder()
                .id(documentType.getId())
                .name(documentType.getName())
                .code(documentType.getCode())
                .description(documentType.getDescription())
                .companyId(documentType.getCompany().getId())
                .fields(fieldDTOs)
                .isActive(documentType.isActive())
                .build();
    }

    public DocumentFieldDTO fieldToDTO(DocumentField field) {
        return DocumentFieldDTO.builder()
                .id(field.getId())
                .name(field.getName())
                .fieldKey(field.getFieldKey())
                .fieldType(field.getFieldType())
                .required(field.isRequired())
                .description(field.getDescription())
                .defaultValue(field.getDefaultValue())
                .validationRules(field.getValidationRules())
                .options(field.getOptions())
                .displayOrder(field.getDisplayOrder())
                .active(field.isActive())
                .build();
    }
} 