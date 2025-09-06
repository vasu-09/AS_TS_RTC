package com.om.Real_Time_Communication.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter @Setter
public class RtcJwtConfig {
    private String issuer;
    private String jwksUri;

    private String audience;

    public RtcJwtConfig() {
    }

    public RtcJwtConfig(String issuer, String jwksUri, String audience) {
        this.issuer = issuer;
        this.jwksUri = jwksUri;
        this.audience = audience;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}