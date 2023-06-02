/**
 * Copyright 2009-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ebf.security.guard;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class PermissionAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        // no security attributes to check, abstain...
        if (CollectionUtils.isEmpty(attributes)) {
            return ACCESS_ABSTAIN;
        }

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // security attributes are set but the authentication has no granted authorities, deny access...
        if (CollectionUtils.isEmpty(authorities)) {
            return ACCESS_DENIED;
        }

        boolean hasSufficientAuthority = attributes.stream().anyMatch(configAttr -> authentication
                .getAuthorities().stream().filter(authority -> authority.getAuthority()
                        .equals(configAttr.getAttribute())
                ).count() == 1
        );

        return hasSufficientAuthority ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return PermissionSecurityAttribute.class.isAssignableFrom(attribute.getClass());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
