<#-- esign_otp.ftl -->
<#-- Variables esperadas:
  appName        (String)  - p.ej. "Proyecto"
  userName       (String?) - nombre del usuario (opcional)
  code           (String)  - código en claro, p.ej. "AB12-7A"
  expiresAt      (String)  - ISO-8601 o legible, p.ej. "2025-08-12 21:00 (UTC-5)"
  documentId     (String?) - id del documento (opcional)
  supportEmail   (String?) - correo soporte (opcional)
-->

<!DOCTYPE html>
<html lang="es" style="margin:0;padding:0;">
<head>
  <meta charset="UTF-8">
  <title>Tu código para firmar - ${appName!''}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <style>
    /* Estilos básicos inline-safe para la mayoría de clientes */
    body { margin:0; padding:0; background:#f5f7fb; font-family: Arial, sans-serif; color:#222; }
    .container { max-width:600px; margin:0 auto; padding:24px; }
    .card { background:#ffffff; border-radius:12px; padding:24px; box-shadow:0 4px 16px rgba(0,0,0,0.06); }
    .brand { font-size:18px; font-weight:700; letter-spacing:0.3px; color:#1e293b; }
    .muted { color:#6b7280; font-size:13px; }
    h1 { font-size:20px; margin:16px 0 8px; }
    p { font-size:14px; line-height:1.6; margin:8px 0; }
    .code-wrap { text-align:center; margin:24px 0; }
    .code-box {
      display:inline-block; font-size:28px; letter-spacing:2px; font-weight:700;
      padding:14px 22px; border-radius:10px; border:1px solid #e5e7eb; background:#f9fafb;
      font-family: "SFMono-Regular", Menlo, Consolas, "Liberation Mono", monospace;
    }
    .info { background:#f8fafc; border:1px dashed #e2e8f0; padding:12px; border-radius:10px; font-size:13px; color:#334155; }
    .footer { text-align:center; margin-top:18px; font-size:12px; color:#94a3b8; }
    .btn {
      display:inline-block; background:#111827; color:#ffffff !important; text-decoration:none;
      padding:10px 16px; border-radius:8px; font-size:14px; margin-top:8px;
    }
    @media (max-width: 640px) {
      .card { padding:18px; }
      .code-box { font-size:24px; }
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="brand">${appName!''}</div>

    <div class="card">
      <h1>Código para firmar tu documento</h1>
      <p>Hola<#if userName?has_content>, ${userName}</#if>,</p>
      <p>Usa este código para confirmar tu firma electrónica en <strong>${appName!''}</strong>.</p>

      <div class="code-wrap">
        <div class="code-box">${code}</div>
      </div>

      <#if documentId?has_content>
      <p class="muted">Documento: <strong>${documentId}</strong></p>
      </#if>

      <div class="info">
        <p>⚠️ <strong>Vigencia:</strong> este código expira el <strong>${expiresAt}</strong>.</p>
        <p>🔒 Por tu seguridad, el código es de un solo uso. No lo compartas con nadie.</p>
      </div>

      <p>Si tú no solicitaste este código, puedes ignorar este correo. Tu cuenta se mantendrá segura.</p>

      <#if supportEmail?has_content>
      <p class="muted">¿Necesitas ayuda? Escríbenos a <a href="mailto:${supportEmail}">${supportEmail}</a>.</p>
      </#if>

      <p>Gracias,<br/>El equipo de ${appName!''}</p>
    </div>

    <div class="footer">
      © ${.now?string["yyyy"]} ${appName!''}. Todos los derechos reservados.
    </div>
  </div>
</body>
</html>
