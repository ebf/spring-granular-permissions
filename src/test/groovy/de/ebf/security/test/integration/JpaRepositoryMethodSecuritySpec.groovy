package de.ebf.security.test.integration

import groovy.json.JsonSlurper

import org.apache.http.client.fluent.Request
import org.springframework.boot.SpringApplication

import spock.lang.Shared
import spock.lang.Specification
import de.ebf.security.jwt.testapp.TestApplicationWithJpaRepositories

class JpaRepositoryMethodSecuritySpec extends Specification {

    @Shared
    def app

    def setupSpec() {
        app = SpringApplication.run(TestApplicationWithJpaRepositories)
    }
    def cleanupSpec() {
        app.stop()
        app.close()
    }
    def "should respond with 403 when an attempt to access a protected jpa repository without the needed permissions is made" () {
        setup:
        def encoder = Base64.encoder
        def authBytes = encoder.encode("test:test".bytes)
        def authString = new String(authBytes);

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/models")
                .addHeader("Authorization", "Basic $authString")
                .execute().returnResponse()
        then:
        returnResponse.statusLine.statusCode == 403
    }

    def "should respond with 200 when an attempt to access a protected jpa repository with the needed permissions is made"() {
        setup:
        def encoder = Base64.encoder
        def authBytes = encoder.encode("user:user".bytes)
        def authString = new String(authBytes);
        def jsonSlurper = new JsonSlurper()

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/models")
                .addHeader("Authorization", "Basic $authString")
                .execute().returnResponse()

        and:
        def json = jsonSlurper.parseText returnResponse.entity.content.text

        println json

        then:
        returnResponse.statusLine.statusCode == 200
        json.page.totalElements == 2
    }
}
