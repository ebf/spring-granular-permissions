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

It checks the spring security provided `Authentication` instance for sufficient permissions by invoking `Authentication#getAuthorities()`.

## What does it not do?

SGP doesn't tell you what type of authentication to use or how your DB schema should look like. 

It's up to you to provide the other pieces of the app that make use of the permission system. (User and role management, authentication etc.)

## Are there any preconditions?

Yes, in order to store the `Permissions` at app startup SGP needs the `EntityManager` bean to be available and the `@EntityScan` annotation to be configured correctly.

Also, the app will fail to start if no transaction manager is configured.


## How do I configure all this?

- add the SGP dependency
	- gradle: `compile("de.ebf:spring-granular-permissions:1.0.0")`
	
	- maven:

	```xml
	<dependency>
		<groupId>de.ebf</groupId>
		<artifactId>spring-granular-permissions</artifactId>
		<version>1.0.2</version>
	</dependency>
	```

- make sure the `@EntityScan` annotation is present in your DB configuration and points to the package(s) of your DB models.

```java
@Configuration
@EntityScan(basePackageClasses = { BaseModel.class })
public class MyDbConfiguration{
  
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

- configure SGP by importing the `PermissionConfig.class` and telling the `PermissionScanner` where to scan for resources and permissions

```java
import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;

@Configuration
@Import({ PermissionsConfig.class })
public class SGPConfiguration {
    @Bean
    public PermissionScanner permissionScanner() {
        DefaultPermissionScanner defaultPermissionScanner = new DefaultPermissionScanner();
        //tell the permission scanner where to scan for protected resources and permissions
        defaultPermissionScanner.setBasePackage(getClass().getPackage().getName());
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

## Which errors/exceptions are thrown?

Like with the [What does it do?](https://github.com/ebf/spring-granular-permissions#what-does-it-do) section, this is split in the same two parts:

#### Permission gathering exceptions

They are all thrown at startup and will prevent your app from starting:

- `NoPermissionModelFoundException`

> When no entity is marked with `PermissionModel` annotation

- `MoreThanOnePermissionModelFoundException`

> When more than one entity is marked with `PermissionModel` annotation

- `NoPermissionFieldNameFoundException`

> When the `PermissionModel` entity has no field marked with `PermissionNameField`

- `MoreThanOnePermissionNameFieldFoundException`

> When the `PermissionModel` entity has more than one field marked with `PermissionNameField`

#### Guarding protected resources exceptions

- `AccessDeniedException`

> When the `Authentication` instance doesn't hold sufficient autority

Note:

Spring Boot's autoconfiguration will make the rest resources respond with 403 Unauthorized  when `AccessDeniedException` is thrown.

## Any more examples?

A very dumb sample app can be found in [test code](https://github.com/ebf/spring-granular-permissions/tree/master/src/test/java/de/ebf/security/jwt/testapp).
