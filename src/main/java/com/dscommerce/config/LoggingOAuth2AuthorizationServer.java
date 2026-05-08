package com.dscommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

public class LoggingOAuth2AuthorizationServer implements OAuth2AuthorizationService {

    private final Logger log = LoggerFactory.getLogger(LoggingOAuth2AuthorizationServer.class);

    private final OAuth2AuthorizationService delegate;

    public LoggingOAuth2AuthorizationServer(OAuth2AuthorizationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        log.info("SAVE called - id={} grantType={}",
                authorization.getId(),
                authorization.getAuthorizationGrantType().getValue());
        delegate.save(authorization);
        log.info("SAVE done - id={}", authorization.getId());
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        log.info("REMOVE called — id={}", authorization.getId());
        delegate.remove(authorization);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        OAuth2Authorization result = delegate.findById(id);
        log.info("FIND_BY_ID id={} found={}", id, result != null);
        return result;
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        OAuth2Authorization result = delegate.findByToken(token, tokenType);
        log.info("FIND_BY_TOKEN type={} found={} id={}",
                tokenType != null ? tokenType.getValue() : "null",
                result != null,
                result != null ? result.getId() : "null");
        return result;
    }
}
