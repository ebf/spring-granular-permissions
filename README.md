# spring-granular-permissions

## What is this?

SGP is a library that brings [Activity Based Authorization](https://lostechies.com/derickbailey/2011/05/24/dont-do-role-based-authorization-checks-do-activity-based-checks/) to Spring Boot apps.

## Should I use it?

Are you developing a Spring Boot app that needs a flexible, dynamic and highly configurable permission system?

## What does it do?

SGP functionality consists of two equally important parts: Gathering permissions and guarding protected resources.

#### Gathering permissions

SGP scans the configured app package for `ProtectedResource`s and their `Permission`s.
Once it finds a `Permission` it stores it into the configured `PermissionModel`'s `PermissionNameField`. 

This process happens at every app startup. This makes for a very nice experience when new features are added that should be protected, they are automatically picked up and ready.

This feature can also be disabled, which can be useful for test purposes.

#### Guarding protected resources

SGP plugs into your app, adds a spring security `AccessDecisionManager` and a `MethodSecurityMetadataSource` and enables global method security (Spring Boot feature).

## What does it not do?

SGP doesn't tell you what type of authentication to use or how your DB schema should look like. 

It's up to you to provide the other pieces of the app that make use of the permission system. (User and role management, authentication etc.)

## How do I configure all this?

- add the SGP dependency
	- gradle: `compile("de.ebf:spring-granular-permissions:0.0.7")`
	
	- maven:

	```xml
	<dependency>
		<groupId>de.ebf</groupId>
		<artifactId>spring-granular-permissions</artifactId>
		<version>0.0.7</version>
	</dependency>
	```

- import the `PermissionsConfig`

```java
@Configuration
@Import({ PermissionsConfig.class })
public class MyConfiguration {

}
```

- Configure a domain model to be used for permission storage

```java
import javax.persistence.Entity;
import javax.persistence.Id;

import de.ebf.security.annotations.PermissionModel;
import de.ebf.security.annotations.PermissionNameField;

@Entity
@PermissionModel
public class Model {

    @Id
    @PermissionNameField
    private String name;

    private String otherField;

    ...
}
```

- configure the scanner

```java
import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;

@Configuration
public class ScannerConfiguration {
    @Bean
    public PermissionScanner permissionScanner() {
        DefaultPermissionScanner defaultPermissionScanner = new DefaultPermissionScanner();
        //tell the permission scanner where to scan for protected resources and permissions
        defaultPermissionScanner.setBasePackage("com.example.app");
        return defaultPermissionScanner;
    }
}
```

- protect some resources

```java
@RestController
@ProtectedResource
public class TestController {

    @RequestMapping(path = "/")
    @Permission("test:request")
    public void testRequest() {
    	//i will only be executed if the security context contains an authority with the name "test:request"
    }

}
```

That's it.
