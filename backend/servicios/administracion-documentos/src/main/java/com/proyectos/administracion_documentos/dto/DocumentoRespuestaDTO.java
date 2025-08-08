package com.proyectos.administracion_documentos.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRespuestaDTO {
    private Long id;
    private String nombre;
    private String proyectoId;
    private String urlDescarga;
}
