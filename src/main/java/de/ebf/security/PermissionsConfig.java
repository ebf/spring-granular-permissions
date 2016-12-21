package de.ebf.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import de.ebf.security.annotations.PermissionModel;
import de.ebf.security.annotations.ProtectedResource;
import de.ebf.security.init.InitPermissions;
import de.ebf.security.internal.conditional.InitPermissionsDisable;
import de.ebf.security.internal.services.PermissionModelFinder;
import de.ebf.security.internal.services.PermissionModelOperations;
import de.ebf.security.internal.services.impl.DefaultPermissionModelFinder;
import de.ebf.security.internal.services.impl.InterfaceBeanScanner;
import de.ebf.security.internal.services.impl.ReflectivePermissionModelOperations;

@Configuration
@Import({ MethodSecurityConfiguration.class })
public class PermissionsConfig {

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
