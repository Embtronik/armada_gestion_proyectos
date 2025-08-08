package com.proyectos.notificaciones.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyectos.notificaciones.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

}
