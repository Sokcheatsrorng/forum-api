package com.forum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${media.server-path}")
    private String mediaServerPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(mediaServerPath)
                .toAbsolutePath()
                .toString()
                .replace("\\", "/");

        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}