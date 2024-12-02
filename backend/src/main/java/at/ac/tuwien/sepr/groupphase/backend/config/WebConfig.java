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
        Path uploadDir2 = Paths.get("./merchandise").toAbsolutePath().normalize();
        registry.addResourceHandler("/merchandise/**")
            .addResourceLocations("file:" + uploadDir2 + "/");
        Path sampleDir = Paths.get("./sampleMerch").toAbsolutePath().normalize();
        registry.addResourceHandler("/merchandise/**")
            .addResourceLocations("file:" + sampleDir + "/");
    }
}
