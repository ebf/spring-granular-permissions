package de.ebf.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import de.ebf.security.jwt.annotations.PermissionModel;
import de.ebf.security.jwt.annotations.ProtectedResource;
import de.ebf.security.jwt.init.InitPermissions;
import de.ebf.security.jwt.internal.conditional.InitPermissionsDisable;
import de.ebf.security.jwt.internal.services.PermissionModelFinder;
import de.ebf.security.jwt.internal.services.PermissionModelOperations;
import de.ebf.security.jwt.internal.services.impl.DefaultPermissionModelFinder;
import de.ebf.security.jwt.internal.services.impl.InterfaceBeanScanner;
import de.ebf.security.jwt.internal.services.impl.ReflectivePermissionModelOperations;

@Configuration
@Import({ HttpSecurityConfiguration.class, MethodSecurityConfiguration.class })
public class JWTPermissionsConfig {

    @Bean
    public PermissionModelFinder permissionModelFinder() {
        return new DefaultPermissionModelFinder();
    }

    @Bean
    public PermissionModelOperations permissionModelOperations() {
        return new ReflectivePermissionModelOperations();
    }

    @Bean
    public InterfaceBeanScanner permissionModelInterfaceBeanScanner() {
        InterfaceBeanScanner interfaceBeanScanner = new InterfaceBeanScanner();

        interfaceBeanScanner.addIncludeFilter(new AnnotationTypeFilter(PermissionModel.class));
        return interfaceBeanScanner;
    }

    @Bean
    public InterfaceBeanScanner protectedResourceInterfaceBeanScanner() {
        InterfaceBeanScanner interfaceBeanScanner = new InterfaceBeanScanner();

        interfaceBeanScanner.addIncludeFilter(new AnnotationTypeFilter(ProtectedResource.class));
        return interfaceBeanScanner;
    }

    @Conditional(InitPermissionsDisable.class)
    @Bean
    public InitPermissions initPermissions() {
        return new InitPermissions();
    }

}
