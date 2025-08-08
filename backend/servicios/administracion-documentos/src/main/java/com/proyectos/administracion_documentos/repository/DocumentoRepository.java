package com.proyectos.administracion_documentos.repository;

import com.proyectos.administracion_documentos.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

@Modifying
@Query("UPDATE Documento d SET d.estado = :estado WHERE d.id = :id")
void actualizarEstado(@Param("id") Long id, @Param("estado") Documento.Estado estado);
}
