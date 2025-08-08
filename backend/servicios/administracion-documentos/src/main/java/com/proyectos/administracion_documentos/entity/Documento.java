package com.proyectos.administracion_documentos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documentos")
public class Documento {
    public enum Estado {
        EN_COLA,
        PROCESANDO,
        PROCESADO,
        FALLIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ruta;

    @Column(nullable = false)
    private String proyectoId;

    private LocalDateTime fechaCarga;

    @Enumerated(EnumType.STRING) // Guarda el enum como String en la DB
    @Column(nullable = false)
    private Estado estado = Estado.EN_COLA; // Valor por defecto

    @Column(nullable = false)
    private String nombreAlmacenado; // Nombre único en el sistema de archivos
}