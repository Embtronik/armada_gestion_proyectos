package com.proyectos.notificaciones.controller;

import com.proyectos.notificaciones.dto.NotificationRequestDTO;
import com.proyectos.notificaciones.dto.NotificationResponseDTO;
import com.proyectos.notificaciones.mapper.NotificacionMapper;
import com.proyectos.notificaciones.respository.NotificacionRepository;
import com.proyectos.notificaciones.service.NotificationProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationProducerService producer;
    private final NotificacionRepository repository;
    private final NotificacionMapper mapper; // Inyectar el mapper

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> crear(@Valid @RequestBody NotificationRequestDTO dto) {
        Long id = producer.enqueue(dto);
        var entity = repository.findById(id).orElseThrow();
        return ResponseEntity.ok(mapper.toResponse(entity)); // Usar mapper de instancia
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> detalle(@PathVariable Long id) {
        return repository.findById(id)
                .map(mapper::toResponse) // Referencia a método de instancia
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}