package com.dscommerce.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtKeyConfig {

    @Bean
    public JWKSource<SecurityContext> jwkSource(JwtProperties props) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        if (props.KeyStoreBase64() != null && !props.KeyStoreBase64().isBlank()) {
            byte[] bytes = Base64.getDecoder().decode(props.KeyStoreBase64());
            ks.load(new ByteArrayInputStream(bytes), props.keyStorePassword().toCharArray());
        } else {
            // classpath ou filesystem — para dev/test
            var resource = new ClassPathResource(props.KeyStoreLocation().replace("classpath:", ""));
            ks.load(resource.getInputStream(), props.keyStorePassword().toCharArray());
        }

        RSAPublicKey publicKey = (RSAPublicKey) ks.getCertificate(props.keyAlias()).getPublicKey();
        RSAPrivateKey privateKey = (RSAPrivateKey) ((KeyStore.PrivateKeyEntry)
                ks.getEntry(props.keyAlias(),
                        new KeyStore.PasswordProtection(props
                                .keyStorePassword()
                                .toCharArray())))
                .getPrivateKey();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(props.keyAlias())
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }
}
