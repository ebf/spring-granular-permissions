package de.ebf.security.test.integration

import javax.persistence.EntityManager
import javax.persistence.TypedQuery

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.jwt.testapp.TestApplication
import de.ebf.security.jwt.testapp.models.Model



@ContextConfiguration(classes = TestApplication)
class InitPermissionsIntegrationSpec extends Specification {

    @Autowired
    private EntityManager entityManager;

    def "should persist the single permission into db"() {

        setup:
        TypedQuery<Model> query = entityManager.createQuery("select m from Model m", Model);

        when:
        def models = query.getResultList()

        then:
        models.size() == 2
        models.find { it.name == "test:request" }  != null
        models.find { it.name == "models:findAll" } != null
    }
}
