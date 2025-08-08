package com.proyectos.administracion_documentos.mapper;

import com.proyectos.administracion_documentos.dto.DocumentoRespuestaDTO;
import com.proyectos.administracion_documentos.entity.Documento;

public class DocumentoMapper {
    public static DocumentoRespuestaDTO toDTO(Documento doc) {
        return DocumentoRespuestaDTO.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .proyectoId(doc.getProyectoId())
                .urlDescarga("/api/documentos/descargar/" + doc.getId())
                .build();
    }
}
