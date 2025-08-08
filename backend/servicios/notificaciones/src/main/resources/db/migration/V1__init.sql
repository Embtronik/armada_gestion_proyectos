create table if not exists notificacion (
  id                     bigserial primary key,
  to_email               varchar(320) not null,
  from_email             varchar(320) not null,
  nombre_plantilla       varchar(120) not null,
  metadato_json          text,
  estado                 varchar(30) not null, -- REQUESTED, SENT, FAILED
  intentos               int not null default 0,
  fecha_creacion         timestamptz not null default now(),
  fecha_envio            timestamptz,
  servicio_origen        varchar(120),
  tipo_evento            varchar(120),
  correlacion_id         varchar(120),
  error_ultimo           text
);

create index if not exists idx_notif_estado on notificacion(estado);
create index if not exists idx_notif_correlacion on notificacion(correlacion_id);
