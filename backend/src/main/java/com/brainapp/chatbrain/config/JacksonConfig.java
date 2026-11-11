package com.brainapp.chatbrain.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class JacksonConfig {

    /**
     * Serializes BigDecimal fields as plain strings: "20500.75" (Conforming to ChatBrain K-15)
     */
    @Bean
    public Module bigDecimalAsPlainString() {
        SimpleModule module = new SimpleModule("BigDecimalPlainStringModule");
        module.addSerializer(BigDecimal.class, new JsonSerializer<>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(value.toPlainString());
                }
            }
        });
        return module;
    }
}
