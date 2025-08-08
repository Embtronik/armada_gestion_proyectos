package com.proyectos.administracion_documentos.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoUploadDTO {
    private String proyectoId;
    private MultipartFile archivo;
}
