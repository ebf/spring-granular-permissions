/**
 * Copyright 2009-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ebf.security.jwt.testapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
@Import(TestApplication.class)
public class TestApplicationWithAuthorizedUser extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder encoder = new Pbkdf2PasswordEncoder();

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("test")
                .password(encoder.encode("test"))
                .roles("whatever")
            .and()
                .withUser("user")
                .password(encoder.encode("user"))
                .authorities(
                        new SimpleGrantedAuthority("test:request"),
                        new SimpleGrantedAuthority("models:findAll"),
                        new SimpleGrantedAuthority("test-multiple:request-1")
                )
            .and()
                .passwordEncoder(encoder);
    }

}
