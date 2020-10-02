/*
 * Copyright 2019 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.leos.services.user;

import eu.europa.ec.leos.model.user.User;

import java.util.List;

public interface UserService {
    /**
     * Get the user with the given login.
     *
     * @param login the user login.
     * @return an user object or <code>null</code> if the user is not found.
     */
    public User getUser(String login);
    /**
     * Get list of users with a given key.
     *
     * @param key.
     * @return a list of users and an empty list if no user is found.
     */
    public List<User> searchUsersByKey(String key);
}
