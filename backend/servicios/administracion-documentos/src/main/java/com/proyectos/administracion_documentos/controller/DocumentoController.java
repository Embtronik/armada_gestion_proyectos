package com.proyectos.administracion_documentos.controller;

import com.proyectos.administracion_documentos.dto.DocumentoRespuestaDTO;
import com.proyectos.administracion_documentos.dto.DocumentoUploadDTO;
import com.proyectos.administracion_documentos.entity.Documento;
import com.proyectos.administracion_documentos.service.DocumentoService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    // En DocumentoController.java
    @PostMapping("/subir")
    public ResponseEntity<DocumentoRespuestaDTO> subirDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("proyectoId") String proyectoId) throws Exception {
        DocumentoUploadDTO dto = new DocumentoUploadDTO(proyectoId, archivo);
        return ResponseEntity.ok(documentoService.encolarDocumento(dto));
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<byte[]> descargarDocumento(@PathVariable Long id) throws Exception {
        Documento documento = documentoService.obtenerDocumento(id); // Nuevo método en servicio
        byte[] archivo = documentoService.descargarDocumento(id);
        System.out.println("Nombre del documento a descargar: " + documento.getNombre());
    
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                   "attachment; filename=\"" + documento.getNombre() + "\"") // Nombre original
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(archivo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) throws IOException {
        documentoService.borrarDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
