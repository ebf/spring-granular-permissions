package de.ebf.security.test.integration



import org.apache.http.client.fluent.Request
import org.springframework.boot.SpringApplication

import spock.lang.Specification
import de.ebf.security.jwt.testapp.TestApplication
import de.ebf.security.jwt.testapp.TestApplicationWithAuthorizedUser

class MethodSecuritySpec extends Specification{

    def "http request to / should result in 403" () {

        setup:
        def app = SpringApplication.run(TestApplication)
        def encoder = Base64.encoder
        def authBytes = encoder.encode("test:test".bytes)
        def authString = new String(authBytes);

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/").addHeader("Authorization", "Basic $authString").execute().returnResponse()
        then:
        returnResponse.statusLine.statusCode == 403

        cleanup:
        app.stop()
        app.close()
    }

    def "http request to / should result in 200 when the user has permission" () {

        setup:
        def app = SpringApplication.run(TestApplicationWithAuthorizedUser)
        def encoder = Base64.encoder
        def authBytes = encoder.encode("user:user".bytes)
        def authString = new String(authBytes);

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/").addHeader("Authorization", "Basic $authString").execute().returnResponse()
        then:
        returnResponse.statusLine.statusCode == 200

        cleanup:
        app.stop()
        app.close()
    }
}
