package at.ac.tuwien.sepr.groupphase.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityPropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "security.auth")
    protected Auth auth() {
        return new Auth();
    }

    @Bean
    @ConfigurationProperties(prefix = "security.jwt")
    protected Jwt jwt() {
        return new Jwt();
    }

    public static class Auth {

        private String header;
        private String prefix;
        private String loginUri;
        private int maxLoginAttempts;
        private Long expirationTimeResetToken;
        private int maxResetCodeAttempts;
        private int maxResetTokenRequests;
        private Long resetTokenResendInterval;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getLoginUri() {
            return loginUri;
        }

        public void setLoginUri(String loginUri) {
            this.loginUri = loginUri;
        }

        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }

        public void setMaxLoginAttempts(int maxLoginAttempts) {
            this.maxLoginAttempts = maxLoginAttempts;
        }

        public Long getExpirationTimeResetToken() {
            return expirationTimeResetToken;
        }

        public void setExpirationTimeResetToken(Long expirationTimeResetToken) {
            this.expirationTimeResetToken = expirationTimeResetToken;
        }

        public int getMaxResetCodeAttempts() {
            return maxResetCodeAttempts;
        }

        public void setMaxResetCodeAttempts(int maxResetCodeAttempts) {
            this.maxResetCodeAttempts = maxResetCodeAttempts;
        }

        public int getMaxResetTokenRequests() {
            return maxResetTokenRequests;
        }

        public void setMaxResetTokenRequests(int maxResetTokenRequests) {
            this.maxResetTokenRequests = maxResetTokenRequests;
        }

        public Long getResetTokenResendInterval() {
            return resetTokenResendInterval;
        }

        public void setResetTokenResendInterval(Long resetTokenResendInterval) {
            this.resetTokenResendInterval = resetTokenResendInterval;
        }
    }

    public static class Jwt {

        private String secret;
        private String type;
        private String issuer;
        private String audience;
        private Long expirationTime;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public Long getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(Long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }
}
