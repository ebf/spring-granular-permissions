package de.ebf.security.jwt.internal.services.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import de.ebf.security.jwt.annotations.Permission;
import de.ebf.security.jwt.internal.services.PermissionScanner;
import de.ebf.security.jwt.permission.BasicPermission;
import de.ebf.security.jwt.permission.InternalPermission;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class DefaultPermissionScanner implements PermissionScanner {

    @Autowired
    private InterfaceBeanScanner protectedResourceInterfaceBeanScanner;

    private String basePackageName;

    public void setBasePackage(String basePackageName) {
        this.basePackageName = basePackageName;
    }

    @Override
    public Set<InternalPermission> scan() {
        Set<InternalPermission> systemFunctionNames = new HashSet<>();
        for (BeanDefinition bd : protectedResourceInterfaceBeanScanner.findCandidateComponents(basePackageName)) {
            try {
                final Class clazz = Class.forName(bd.getBeanClassName());
                if (clazz == null) {
                    continue;
                }
                Method[] declaredMethods = clazz.getDeclaredMethods();

                for (Method systemFunctionResource : declaredMethods) {

                    Method mostSpecificMethod = ClassUtils.getMostSpecificMethod(systemFunctionResource, clazz);

                    Permission systemFunctionNameHolder = AnnotationUtils.findAnnotation(mostSpecificMethod,
                            Permission.class);

                    if (systemFunctionNameHolder == null) {
                        continue;
                    }

                    systemFunctionNames.add(new BasicPermission(systemFunctionNameHolder.value()));
                }

            } catch (ClassNotFoundException e) {
            }
        }

        return systemFunctionNames;
    }

}
