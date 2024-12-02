package at.ac.tuwien.sepr.groupphase.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("./newsImages").toAbsolutePath().normalize();
        registry.addResourceHandler("/newsImages/**")
            .addResourceLocations("file:" + uploadDir + "/");
    }
}
