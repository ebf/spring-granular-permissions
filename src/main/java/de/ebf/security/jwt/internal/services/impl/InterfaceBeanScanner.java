package de.ebf.security.jwt.internal.services.impl;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 *
 * ClassPathScanningCandidateComponentProvider that supports scanning for
 * interfaces too.
 *
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class InterfaceBeanScanner extends ClassPathScanningCandidateComponentProvider {

    public InterfaceBeanScanner() {
        super(false);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }

}