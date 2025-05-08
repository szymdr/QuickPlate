package com.quickplate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:3000","http://localhost:5173")
      .allowedMethods("*")
      .allowedHeaders("*")
      .exposedHeaders("Authorization");
  }
}