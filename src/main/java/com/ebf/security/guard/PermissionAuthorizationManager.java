package com.ebf.security.guard;

import io.micrometer.observation.ObservationRegistry;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.ObservationAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Implementation of the {@link AuthorizationManager} that would check if the {@link MethodInvocation},
 * decorated by the {@link com.ebf.security.annotations.Permission}, can proceed.
 * <p>
 * The manager would extract the permissions which are required to perform the invocation via
 * the {@link PermissionSecurityAttributeRegistry} and compare them with {@link GrantedAuthority authorities}
 * that the current {@link Authentication} has.
 * <p>
 * In case the method has multiple permissions set, the manager would proceed with the invocation when
 * the {@link Authentication} contains at least one {@link GrantedAuthority} matching the permissions.
 *
 * @author : vladimir.spasic@ebf.com
 * @since : 29.09.23, Fri
 **/
public class PermissionAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    private final PermissionSecurityAttributeRegistry registry;

    public static AuthorizationManager<MethodInvocation> create() {
        return new PermissionAuthorizationManager();
    }

    public static AuthorizationManager<MethodInvocation> create(ObjectProvider<ObservationRegistry> provider) {
        return new DeferringObservationAuthorizationManager(provider);
    }

    private PermissionAuthorizationManager() {
        registry = new PermissionSecurityAttributeRegistry();
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation invocation) {
        final Collection<String> permissions = registry.get(invocation);

        // no security attributes to check, abstain...
        if (CollectionUtils.isEmpty(permissions)) {
            return null;
        }

        final Collection<? extends GrantedAuthority> authorities = authentication.get().getAuthorities();

        // security attributes are set but the authentication has no granted authorities, deny access...
        if (CollectionUtils.isEmpty(authorities)) {
            return new AuthorizationDecision(false);
        }

        boolean hasSufficientAuthority = permissions.stream().anyMatch(permission -> authorities
                .stream()
                .filter(authority -> authority.getAuthority().equals(permission))
                .count() == 1
        );

        return new AuthorizationDecision(hasSufficientAuthority);
    }

    private static class DeferringObservationAuthorizationManager implements AuthorizationManager<MethodInvocation> {
        private final Supplier<AuthorizationManager<MethodInvocation>> delegate;

        DeferringObservationAuthorizationManager(ObjectProvider<ObservationRegistry> provider) {
            this(provider, new PermissionAuthorizationManager());
        }

        DeferringObservationAuthorizationManager(ObjectProvider<ObservationRegistry> provider,
                                                 AuthorizationManager<MethodInvocation> delegate) {
            this.delegate = SingletonSupplier.of(() -> {
                ObservationRegistry registry = provider.getIfAvailable(() -> ObservationRegistry.NOOP);
                if (registry.isNoop()) {
                    return delegate;
                }
                return new ObservationAuthorizationManager<>(registry, delegate);
            });
        }

        @Override
        public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation object) {
            return this.delegate.get().check(authentication, object);
        }
    }
}
