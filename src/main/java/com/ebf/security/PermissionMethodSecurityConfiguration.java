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
package com.ebf.security;

import com.ebf.security.guard.PermissionAccessDecisionVoter;
import com.ebf.security.guard.PermissionMetadataSource;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
@EnableGlobalMethodSecurity
public class PermissionMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new PermissionMetadataSource();
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        final AffirmativeBased manager = (AffirmativeBased) super.accessDecisionManager();
        final List<AccessDecisionVoter<?>> voters = new ArrayList<>(manager.getDecisionVoters());
        voters.add(new PermissionAccessDecisionVoter());

        return new AffirmativeBased(voters);
    }
}
