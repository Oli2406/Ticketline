package at.ac.tuwien.sepr.groupphase.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/newsImages/**")
            .addResourceLocations("classpath:/newsImages/");

        registry.addResourceHandler("/merchandise/**")
            .addResourceLocations("classpath:/merchandise/");
    }
}