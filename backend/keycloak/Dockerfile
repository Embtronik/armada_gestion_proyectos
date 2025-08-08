# Etapa 1: Construcción con scripts habilitados
FROM quay.io/keycloak/keycloak:26.1.4 as builder

ENV KC_DB=postgres
WORKDIR /opt/keycloak

# Habilita scripts (útil para personalizaciones)
RUN /opt/keycloak/bin/kc.sh build --features=scripts

# Etapa 2: Imagen final
FROM quay.io/keycloak/keycloak:26.1.4

# Copiar Keycloak ya construido
COPY --from=builder /opt/keycloak/ /opt/keycloak/

# DESARROLLO: No se incluye certificado
# PRODUCCIÓN: Montar certificado TLS
# COPY ./certs/wildcard_entidad_gov_co.pfx /opt/keycloak/certs/wildcard_entidad_gov_co.pfx

# DESARROLLO: Puedes dejar esto si vas a crear temas
# COPY --chown=keycloak:keycloak ./themes/rdm-theme /opt/keycloak/themes/rdm-theme

# Configuración base de datos (igual para desarrollo y producción)
ENV KC_DB=postgres
ENV KC_DB_URL=jdbc:postgresql://postgres:5432/proyectos
ENV KC_DB_USERNAME=proyectos_user
ENV KC_DB_PASSWORD=Arm@d@2025
ENV KC_DB_SCHEMA=keycloak_schema

# DESARROLLO: Puedes usar localhost o nombre de servicio interno
# PRODUCCIÓN: Descomenta y configura el hostname real
# ENV KC_HOSTNAME=sso.entidad.gov.co

# PRODUCCIÓN: Configurar HTTPS con certificado
# ENV KC_HTTPS_KEY_STORE_FILE=/opt/keycloak/certs/wildcard_entidad_gov_co.pfx
# ENV KC_HTTPS_KEY_STORE_TYPE=PKCS12

# Admin por defecto (puedes cambiarlo luego)
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin123

EXPOSE 8443

# Comando de arranque
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev"]
# PRODUCCIÓN: Usa este en lugar del anterior
# CMD ["start", "--optimized"]
