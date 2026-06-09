package com.ternura.config;

import com.ternura.model.enums.WordleDifficulty;
import com.ternura.model.enums.WordleMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {
    // 針對 body 參數無視 enum 大小寫序列化轉換
    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        JsonMapper jsonMapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .build();
        builder.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper));
    }

    // 針對 param 參數無視 enum 大小寫序列化轉換
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, WordleMode.class,
                source -> WordleMode.valueOf(source.toUpperCase()));
        registry.addConverter(String.class, WordleDifficulty.class,
                source -> WordleDifficulty.valueOf(source.toUpperCase()));
    }
}