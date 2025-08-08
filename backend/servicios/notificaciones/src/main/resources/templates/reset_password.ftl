<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>${subject!'-'}</title></head>
<body style="font-family: Arial, sans-serif; color: #333;">
  <p>Hola ${nombreUsuario!''},</p>
  <p>Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace:</p>
  <p><a href="${link}">${link}</a></p>
  <p>Este enlace expira en ${minutosExpiracion!30} minutos.</p>
  <hr/>
  <small>Enviado por ${servicioOrigen!'Sistema'} • Correlación: ${correlacionId!'-'}</small>
</body>
</html>
