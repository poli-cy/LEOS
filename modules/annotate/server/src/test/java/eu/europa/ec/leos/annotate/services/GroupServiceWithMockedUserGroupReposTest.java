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
package eu.europa.ec.leos.annotate.services;

import eu.europa.ec.leos.annotate.helper.TestDbHelper;
import eu.europa.ec.leos.annotate.model.UserInformation;
import eu.europa.ec.leos.annotate.model.entity.Group;
import eu.europa.ec.leos.annotate.model.entity.User;
import eu.europa.ec.leos.annotate.repository.*;
import eu.europa.ec.leos.annotate.services.impl.GroupServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.config.name=anot")
@WebAppConfiguration
@ActiveProfiles("test")
public class GroupServiceWithMockedUserGroupReposTest {

    // -------------------------------------
    // Required services and repositories
    // -------------------------------------

    // the UserGroupRepository used inside the GroupService is mocked
    @Mock
    private UserGroupRepository userGroupRepos;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Before
    public void setupTests() {

        MockitoAnnotations.initMocks(this);

        TestDbHelper.cleanupRepositories(this);
    }

    @After
    public void cleanDatabaseAfterTests() {

        TestDbHelper.cleanupRepositories(this);
    }

    // -------------------------------------
    // Tests
    // -------------------------------------

    /**
     * test return value null is received when no groups are found for an unknown user
     */
    @Test
    public void testNoGroupsForUser() {

        final long userId = 8;

        final User unknownUser = new User("unknown");
        unknownUser.setId(userId);

        Mockito.when(userGroupRepos.findByUserId(userId)).thenReturn(null);

        // ask with a user that is not known - returns null for groups
        Assert.assertNull(groupService.getGroupsOfUser(unknownUser));

        // and returns null for same as Json
        Assert.assertNull(groupService.getUserGroupsAsJson(new UserInformation(unknownUser, "someauthority")));
    }

    /**
     * test return value is null when no users/groups are found for a given group
     */
    @Test
    public void testNoUserIdsForUnknownGroup() {

        final long groupId = 1;

        final Group unknownGroup = new Group();
        unknownGroup.setId(groupId);

        Mockito.when(userGroupRepos.findByGroupId(groupId)).thenReturn(null);

        // null should be returned for an unknown group (unknown in DB)
        Assert.assertNull(groupService.getUserIdsOfGroup(unknownGroup));
    }

    /**
     * test return value is null when no users/groups are found for a given user
     */
    @Test
    public void testNoGroupIdsForUnknownUser() {

        final long userId = 1;

        final User unknownUser = new User();
        unknownUser.setId(userId);

        Mockito.when(userGroupRepos.findByUserId(userId)).thenReturn(null);

        // null should be returned for an unknown user (unknown in DB)
        Assert.assertNull(groupService.getGroupIdsOfUser(unknownUser));
    }

}
