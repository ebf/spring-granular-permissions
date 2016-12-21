package de.ebf.security.jwt.testapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ebf.security.annotations.Permission;
import de.ebf.security.annotations.ProtectedResource;

@RestController
@ProtectedResource
public class TestController {

    @RequestMapping(path = "/")
    @Permission("test:request")
    public void testRequest() {

    }

}
