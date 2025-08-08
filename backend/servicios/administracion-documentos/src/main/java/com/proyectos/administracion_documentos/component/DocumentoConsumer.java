package com.proyectos.administracion_documentos.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.proyectos.administracion_documentos.entity.Documento;
import com.proyectos.administracion_documentos.repository.DocumentoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentoConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentoConsumer.class);
    private final DocumentoRepository documentoRepository;

    // Este método lo invoca el MessageListenerAdapter (ver RabbitConfig) y ya recibe un Long
    public void onMessage(Long documentoId) {
        if (documentoId == null) {
            log.warn("Mensaje recibido con documentoId null; se ignora.");
            return;
        }

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + documentoId));

        try {
            documento.setEstado(Documento.Estado.PROCESANDO);
            documentoRepository.save(documento);

            procesarDocumento(documento); // Lógica pesada

            documento.setEstado(Documento.Estado.PROCESADO);
            documentoRepository.save(documento);
            log.info("Documento {} procesado correctamente", documentoId);

        } catch (Exception e) {
            log.error("Fallo procesando documento {}: {}", documentoId, e.getMessage(), e);
            documento.setEstado(Documento.Estado.FALLIDO);
            documentoRepository.save(documento);
            // si quieres requeuear, lanza la excepción; si no, ya quedó persistido como FALLIDO
            // throw e;
        }
    }

    private void procesarDocumento(Documento documento) throws InterruptedException {
        log.info("Procesando: {}", documento.getRuta());
        // TODO: lógica real
        Thread.sleep(5000); // Simulación
    }
}
