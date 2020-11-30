/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.yangbajing.portal.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties oAuth2ResourceServerProperties/*, WebClient.Builder webClientBuilder*/) {
        OAuth2ResourceServerProperties.Jwt properties = oAuth2ResourceServerProperties.getJwt();
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(properties.getJwkSetUri())
//                .webClient(webClientBuilder.build())
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
        String issuerUri = properties.getIssuerUri();
        if (issuerUri != null) {
            nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
        }
        return nimbusJwtDecoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorize -> authorize.antMatchers("/messages/**").hasAuthority("SCOPE_message.read")
                .anyRequest()
                .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    }
}
