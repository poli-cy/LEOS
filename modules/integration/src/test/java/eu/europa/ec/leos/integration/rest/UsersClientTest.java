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
package eu.europa.ec.leos.integration.rest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import eu.europa.ec.leos.model.user.Entity;
import eu.europa.ec.leos.model.user.User;
import eu.europa.ec.leos.test.support.LeosTest;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

public class UsersClientTest extends LeosTest{
    @Mock
    private RestOperations restTemplate;

    @InjectMocks
    private UsersClientImpl usersClientImpl;

    private String testServerUrl;

    @Before
    public void init() {
        testServerUrl = "http://test/ud-repo";

        ReflectionTestUtils.setField(usersClientImpl, "repositoryUrl", testServerUrl);
        ReflectionTestUtils.setField(usersClientImpl, "findByLoginUri", "/users/{userId}");
        ReflectionTestUtils.setField(usersClientImpl, "searchByKeyUri", "/users?searchKey={searchKey}");
    }

    @Test
    public void test_findUsersByKey() {
        String user1FirstName = "John";
        String user1LastName = "SMITH";
        String user1Login = "smithj";
        String user1Mail = "smithj@test.com";

        String user2FirstName = "Peter";
        String user2LastName = "SURRY";
        String user2Login = "surryp";
        String user2Mail = "surryp@test.com";

        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("1", "EXT.A1", "Ext"));

        String searchKey = "smith";
        
        List<String> roles = new ArrayList<>();

        UserJSON user1 = new UserJSON(user1Login, 1l, user1FirstName, user1LastName, entities, user1Mail, roles);
        UserJSON user2 = new UserJSON(user2Login, 0l, user2FirstName, user2LastName, entities, user2Mail, roles);

        List<UserJSON> users = new ArrayList<UserJSON>();
        users.add(user1);
        users.add(user2);

        String uri = testServerUrl + "/users?searchKey={searchKey}";

        ResponseEntity<List<UserJSON>> responseEntity = new ResponseEntity<List<UserJSON>>((List<UserJSON>) users, HttpStatus.OK);

        Map<String, String> params = new HashMap<String, String>();
        params.put("searchKey", searchKey);

        when(restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserJSON>>() {}, params)).thenReturn(responseEntity);

        List<User> result = usersClientImpl.searchUsers(searchKey);

        assertThat(result, notNullValue());
        assertThat(result.size(), is(2));
        assertEquals(result.get(0).getLogin(),user1Login);
        assertEquals(result.get(0).getName(), user1LastName + " " + user1FirstName);
        assertEquals(result.get(0).getEntities(), entities);
        assertEquals(result.get(0).getId(), Long.valueOf(1));
        assertEquals(((UserJSON) result.get(0)).getRoles(), roles);
        assertEquals(result.get(1).getLogin(),user2Login);
        assertEquals(result.get(1).getName(), user2LastName + " " + user2FirstName);
        assertEquals(result.get(1).getEntities(), entities);
        assertEquals(result.get(1).getId(), Long.valueOf(0));
        assertEquals(((UserJSON) result.get(1)).getRoles(), roles);
    }
 
    @Test
    public void test_findBylogin() {
        String user1FirstName = "John";
        String user1LastName = "SMITH";
        String user1Login = "smithj";
        String user1Mail = "smithj@test.com";

        List<Entity> entities = new ArrayList<>();
        entities.add(new Entity("1", "EXT.A1", "Ext"));

        String userId = "smithj";

        List<String> roles = new ArrayList<>();
        
        UserJSON user1 = new UserJSON(user1Login, 1l, user1FirstName, user1LastName, entities, user1Mail, roles);

        String uri = testServerUrl + "/users/{userId}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);

        when(restTemplate.getForObject(uri, UserJSON.class, params)).thenReturn(user1);

        User result = usersClientImpl.getUserByLogin(userId);

        assertThat(result, notNullValue());
        assertEquals(result.getLogin(),user1Login);
        assertEquals(result.getId(), Long.valueOf(1));
        assertEquals(result.getName(), user1LastName + " " + user1FirstName);
        assertEquals(result.getEntities(), entities);
        assertEquals(((UserJSON) result).getRoles(), roles);
    }
}