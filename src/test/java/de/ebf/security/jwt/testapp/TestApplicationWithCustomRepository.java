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

import de.ebf.security.jwt.testapp.models.Model;
import de.ebf.security.jwt.testapp.repositories.ModelRepository;
import de.ebf.security.repository.PermissionModel;
import de.ebf.security.repository.PermissionModelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

import java.util.Collection;

@Configuration
@Import({TestApplicationWithCustomRepository.RepositoryConfiguration.class, TestApplicationWithAuthorizedUser.class})
@EnableJpaRepositories(basePackages = "de.ebf.security.jwt.testapp.repositories")
public class TestApplicationWithCustomRepository {

    protected static class RepositoryConfiguration {
        @Bean
        public JpaPermissionModelRepository jpaPermissionModelRepository(ModelRepository repository) {
            return new JpaPermissionModelRepository(repository);
        }
    }

    protected static class JpaPermissionModelRepository implements PermissionModelRepository {

        private final ModelRepository repository;

        public JpaPermissionModelRepository(ModelRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PermissionModel> Collection<T> findAll() {
            return Streamable.of(repository.findAll())
                    .map(model -> (T) model)
                    .toSet();
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PermissionModel> T save(@NonNull String permission) {
            final Model model = new Model();
            model.setPermission(permission);
            model.setTimestamp(System.currentTimeMillis());

            return save((T) model);
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PermissionModel> T save(@NonNull T permission) {
            return (T) repository.save((Model) permission);
        }

        @Override
        public <T extends PermissionModel> void delete(@NonNull T permission) {
            repository.delete((Model) permission);
        }

    }
}
