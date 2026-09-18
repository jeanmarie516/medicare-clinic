package com.medicare.dto;

import java.util.Map;

public class ConfigurationDto {
    private Map<String, String> configurations;
    private String message;

    public ConfigurationDto() {}

    public ConfigurationDto(Map<String, String> configurations, String message) {
        this.configurations = configurations;
        this.message = message;
    }

    public Map<String, String> getConfigurations() { return configurations; }
    public void setConfigurations(Map<String, String> configurations) { this.configurations = configurations; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
