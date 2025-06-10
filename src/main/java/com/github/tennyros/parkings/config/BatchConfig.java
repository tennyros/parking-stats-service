package com.github.tennyros.parkings.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "parking.batch")
public class BatchConfig {

    @Min(1)
    @Max(10000)
    private int size = 100;

}