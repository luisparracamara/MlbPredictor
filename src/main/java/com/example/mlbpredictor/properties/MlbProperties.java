package com.example.mlbpredictor.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mlb-properties")
@Getter
@Setter
public class MlbProperties {

    private String season;

}
