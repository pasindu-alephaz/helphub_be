package lk.helphub.api.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Value("${app.upload.dir:./uploads/profile-pictures}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/v1/auth/verification/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files as static resources
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        // Serve uploaded profile pictures as static resources
        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Support application/octet-stream for Jackson to handle multipart JSON parts from some clients (like Swagger UI)
        converters.stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .forEach(converter -> {
                    List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                    supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
                    converter.setSupportedMediaTypes(supportedMediaTypes);
                });
    }
}
