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
package de.ebf.security.exceptions;

import de.ebf.security.repository.PermissionModel;

public class NoPermissionModelFoundException extends PermissionModelException {
    private static final long serialVersionUID = -9191745275272816537L;

    public NoPermissionModelFoundException() {
        super("Could not find any Permission Model candidate. Make sure that you have defined one " +
                "Entity that  implements the '" + PermissionModel.class + "' interface");
    }

}
