package com.proyectos.notificaciones.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.StringWriter;
import java.util.Map;

@Service @RequiredArgsConstructor
public class TemplateRenderService {
  private final Configuration freemarkerCfg;

  public String render(String nombrePlantilla, Map<String,Object> model) throws Exception {
    Template template = freemarkerCfg.getTemplate(nombrePlantilla + ".ftl");
    try (StringWriter out = new StringWriter()) {
      template.process(model, out);
      return out.toString();
    }
  }
}
