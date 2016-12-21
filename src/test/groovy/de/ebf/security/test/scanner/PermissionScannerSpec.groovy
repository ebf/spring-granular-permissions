package de.ebf.security.test.scanner

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.jwt.testapp.TestApplication
import de.ebf.security.scanner.PermissionScanner;

@ContextConfiguration(classes = TestApplication)
class PermissionScannerSpec extends Specification{

    @Autowired
    private PermissionScanner permissionScanner

    def "should find permission defined on testcontroller"() {

        when:
        def permissions = permissionScanner.scan()

        then:
        permissions.size() == 1
        permissions[0].name == "test:request"
    }
}
