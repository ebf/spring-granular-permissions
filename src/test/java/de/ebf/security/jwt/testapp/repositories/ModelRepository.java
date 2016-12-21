package de.ebf.security.jwt.testapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.ebf.security.annotations.Permission;
import de.ebf.security.annotations.ProtectedResource;
import de.ebf.security.jwt.testapp.models.Model;

@ProtectedResource
public interface ModelRepository extends PagingAndSortingRepository<Model, String> {

    @Permission("models:findAll")
    @Override
    Page<Model> findAll(Pageable pageable);

}
