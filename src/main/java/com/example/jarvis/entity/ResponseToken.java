package com.example.jarvis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseToken {

    @JsonProperty("access_token")
    private String accessToken;
}
