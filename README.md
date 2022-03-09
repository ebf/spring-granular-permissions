# spring-granular-permissions

## What is this?

SGP is a library that brings [Activity Based Authorization](https://lostechies.com/derickbailey/2011/05/24/dont-do-role-based-authorization-checks-do-activity-based-checks/) to Spring Boot apps.

## Should I use it?

Are you developing a Spring Boot app that needs a flexible, dynamic and highly configurable permission system?

## What does it do?

SGP functionality consists of two equally important parts: Gathering permissions and guarding protected resources.

#### Gathering permissions

SGP scans the configured app package for `ProtectedResource`s and their `Permission`s.
Once it finds a `Permission` it stores it into the JPA Entity that implements the `PermissionModel` interface.

This process happens at every app startup. This makes for a very nice experience when new features are added that should be protected, they are automatically picked up and ready.

This feature can also be disabled, which can be useful for test purposes.

#### Guarding protected resources

SGP plugs into your app, adds a spring security `AccessDecisionManager` and a `MethodSecurityMetadataSource` and enables global method security (Spring Boot feature).

It checks the spring security provided `Authentication` instance for sufficient permissions by invoking `Authentication#getAuthorities()`.

## What does it not do?

SGP doesn't tell you what type of authentication to use or how your DB schema should look like. 

It's up to you to provide the other pieces of the app that make use of the permission system. (User and role management, authentication etc.)

## Are there any preconditions?

Yes, this library uses JPA to manage scanned `Permissions` at app startup. Meaning, it is advisable to have the `EntityManagerFactory` Bean to be available and the `@EntityScan` annotation to be configured correctly.

## How do I configure all this?

#### Add the SGP dependency

For gradle:

```groovy
compile("de.ebf:spring-granular-permissions:2.0.0")
```

For maven:

```xml
<dependency>
	<groupId>de.ebf</groupId>
	<artifactId>spring-granular-permissions</artifactId>
	<version>2.0.0</version>
</dependency>
```

#### Scan JPA entities

Make sure the `@EntityScan` annotation is present in your DB configuration and points to the package(s) of your DB models.

```java
@Configuration
@EntityScan(basePackageClasses = { BaseModel.class })
public class MyDbConfiguration{
  
}
```

##### Configure a domain model to be used for permission storage

```java
import javax.persistence.Entity;
import javax.persistence.Id;

import de.ebf.security.repository.PermissionModel;

@Entity
public class Model extends BaseModel implements PermissionModel {

	@Id
	private String name;

	private String otherField;
    
	/* your other entity fields ... */
	
	/* implement the methods from the PermissionModel interface */
	
	@Override
	public void setPermission(String permission) {
		this.name = permission;
	}

	@Override
	public String getPermission() {
		return name;
	}

	/* your other entity getters and setters ... */
}
```

##### Scan for permissions

Configure SGP by annotating any of your configuration classes by the `PermissionScan` annotation and tell it where to look for the `Permission` annotations on your protected resources.

```java
import de.ebf.security.annotations.PermissionScan;

@Configuration
@PermissionScan
public class SGPConfiguration {

}
```

By default, the `PermissionScan` annotation is going to scan for `Permissions` in the package name that is the same as the class where the annotation is located.

If you wish to change this location, or include other ones, you can use the `basePackageNames` or `basePackageClasses` attributes like so:

```java
import de.ebf.security.annotations.PermissionScan;
import my.classpackage.Type;

@Configuration
@PermissionScan(
    basePackageNames = { "my.package", "my.other.package" },
    basePackageClasses = Type.class
)
public class SGPConfiguration {

}
```

This would scan the following packages for `ProtectedResources` and `Permissions`:
 - `my.package`
 - `my.other.package`
 - `my.classpackage`


##### Protect some resources

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

### Configure permission initialization

The `PermissionScan` is also initializing the permissions that were picked up by the scan using the `PermissionModelRepository`.

Initialization process does the following:

 - Creates permission models that were part of the scan and not yet persisted in the repository
 - Deletes permissions models that are no longer part of the scan but are persisted in the repository

You can choose to omit this process completely by specifying the `strategy` attribute on the `PermissionScan` annotation:

```java
import de.ebf.security.annotations.PermissionScan;

@Configuration
@PermissionScan(strategy = PermissionScan.InitializationStrategy.NONE)
public class SGPConfiguration {

}
```

You can choose when this initialization process should occur in the app startup process:

 - `EARLY`
> As soon as the `PermissionInitializer` Bean is ready

 - `ON_READY` (default)
> When the `ApplicationReadyEvent` is fired

 - `ON_REFRESH`
> When the `ContextRefreshedEvent` is fired

### Fine tuning the initialization

You can also alter the behavior of the initialization phase via providing a bean 
with custom implementation of `de.ebf.security.init.PermissionInitializer` interface.

For example by default `de.ebf.security.init.DefaultPermissionInitializer implements PermissionInitializer`
will do a cleanup for the permissions that previously persisted but no longer exists in the application source code (ex: `@Permission("foo") was part of the app but recently removed and no longer exists`).
In this case if you like to keep those permissions persisted rather than removed by DefaultPermissionInitializer, you may consider to
either implement the `PermissionInitializer` fully or just extend the `DefaultPermissionInitializer`
to override specific functions to alter the behavior according to needs of your application.

```java
// Custom implementation would be
public class MyCustomPermissionsInitializer extends DefaultPermissionInitializer {

    public MyCustomPermissionsInitializer(PermissionModelRepository permissionModelRepository) {
        super(permissionModelRepository);
    }

    // ...

    // Overridden implementation will log any unused permissions 
    // otherwise the default implementation would try to remove them.
    @Override
    protected <T extends PermissionModel> void removePermissions(@NotNull Set<T> permissions) {
        // Just logging the permissions are not in use anymore. 
        // But not removing from the db!
        if (!CollectionUtils.isEmpty(permissions)) {
            // Let us log those permissions during initialization as an information
            log.info(
                "Persisted permission(s) that no longer in use and can be cleaned up are listed below:\n\t{}",
                permissions.stream().map(PermissionModel::getPermission).collect(Collectors.toSet())
            );
        }
    }

    // ...
}

// ... and Bean definition would be
@Configuration
@PermissionScan(
    basePackageNames = { "my.package", "my.other.package" },
    basePackageClasses = Type.class
)
public class SGPConfiguration {
    // ...
    @Bean
    public PermissionInitializer nonInvasivePermissionInitializer(ObjectProvider<PermissionModelRepository> permissionModelRepository) {
        PermissionModelRepository repository = permissionModelRepository.getIfAvailable(() -> {
            throw new FatalBeanException("Bean for PermissionModelRepository not found! Granular Permission can not be initialized!");
        });
        return new MyCustomPermissionsInitializer(repository);
    }
    // ...
}
```

## Which errors/exceptions are thrown?

Like with the [What does it do?](https://github.com/ebf/spring-granular-permissions#what-does-it-do) section, this is split in the same two parts:

#### Permission gathering exceptions

They are all thrown at startup and will prevent your app from starting:

- `NoPermissionModelFoundException`

> When no entity implements the `PermissionModel` interface

- `MoreThanOnePermissionModelFoundException`

> When more than one entity is implementing the `PermissionModel` interface

#### Guarding protected resources exceptions

- `AccessDeniedException`

> When the `Authentication` instance doesn't hold sufficient autority

Note:

Spring Boot's autoconfiguration will make the rest resources respond with 403 Unauthorized  when `AccessDeniedException` is thrown.

## Any more examples?

A very dumb sample app can be found in [test code](https://github.com/ebf/spring-granular-permissions/tree/master/src/test/java/de/ebf/security/jwt/testapp).
