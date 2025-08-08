package com.proyectos.administracion_documentos.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.proyectos.administracion_documentos.config.RabbitConfig;
import com.proyectos.administracion_documentos.dto.DocumentoRespuestaDTO;
import com.proyectos.administracion_documentos.dto.DocumentoUploadDTO;
import com.proyectos.administracion_documentos.entity.Documento;
import com.proyectos.administracion_documentos.mapper.DocumentoMapper;
import com.proyectos.administracion_documentos.repository.DocumentoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {
    private final DocumentoRepository documentoRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${documentos.path-nfs}")
    private String NFS_PATH;

    public DocumentoRespuestaDTO encolarDocumento(DocumentoUploadDTO dto) throws IOException {
        MultipartFile archivo = dto.getArchivo();
        String nombreOriginal = StringUtils.cleanPath(archivo.getOriginalFilename());
        String extension = obtenerExtension(nombreOriginal);
        String nombreAlmacenado = generarNombreUnico(extension);
        String rutaCompleta = NFS_PATH + nombreAlmacenado;

        // Validación básica
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        // Garantizar que el directorio existe
        Path directorio = Paths.get(NFS_PATH);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        // Guardar archivo físicamente
        archivo.transferTo(new File(rutaCompleta));

        Documento documento = Documento.builder()
                .nombre(nombreOriginal)
                .nombreAlmacenado(nombreAlmacenado)
                .ruta(rutaCompleta)
                .proyectoId(dto.getProyectoId())
                .fechaCarga(LocalDateTime.now())
                .estado(Documento.Estado.EN_COLA)
                .build();

        Documento guardado = documentoRepository.save(documento);
        rabbitTemplate.convertAndSend(RabbitConfig.DOCUMENTOS_QUEUE, guardado.getId());
        //rabbitTemplate.convertAndSend(RabbitConfig.DOCUMENTOS_QUEUE, String.valueOf(guardado.getId()));

        return DocumentoMapper.toDTO(guardado);
    }

    public byte[] descargarDocumento(Long id) throws IOException {
        Documento doc = obtenerDocumento(id);
        Path rutaArchivo = Paths.get(doc.getRuta());
        
        if (!Files.exists(rutaArchivo)) {
            throw new IOException("Archivo no encontrado en: " + doc.getRuta());
        }
        
        return Files.readAllBytes(rutaArchivo);
    }

    @Transactional
    public void borrarDocumento(Long id) throws IOException {
        Documento documento = obtenerDocumento(id);
        Path rutaArchivo = Paths.get(documento.getRuta());

        if (Files.exists(rutaArchivo)) {
            Files.delete(rutaArchivo);
        } else {
            throw new IOException("El archivo no existe en: " + documento.getRuta());
        }

        documentoRepository.delete(documento);
        rabbitTemplate.convertAndSend(RabbitConfig.COLA_ELIMINACION, id);
        //rabbitTemplate.convertAndSend(RabbitConfig.COLA_ELIMINACION, String.valueOf(id));
    }

    public Documento obtenerDocumento(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
    }

    // --- Métodos auxiliares privados ---
    private String generarNombreUnico(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String obtenerExtension(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        return ultimoPunto > 0 ? nombreArchivo.substring(ultimoPunto + 1) : "";
    }
}