package de.ebf.security.jwt.testapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Import(TestApplicationWithAuthorizedUser.class)
@EnableJpaRepositories(basePackages = "de.ebf.security.jwt.testapp.repositories")
public class TestApplicationWithJpaRepositories {

}
