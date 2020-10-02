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
package eu.europa.ec.leos.annotate.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.europa.ec.leos.annotate.helper.SerialisationHelper;
import eu.europa.ec.leos.annotate.helper.SpotBugsAnnotations;
import eu.europa.ec.leos.annotate.helper.TestDbHelper;
import eu.europa.ec.leos.annotate.model.entity.Group;
import eu.europa.ec.leos.annotate.model.entity.Token;
import eu.europa.ec.leos.annotate.model.entity.User;
import eu.europa.ec.leos.annotate.model.entity.UserGroup;
import eu.europa.ec.leos.annotate.model.web.token.JsonAuthenticationFailure;
import eu.europa.ec.leos.annotate.repository.GroupRepository;
import eu.europa.ec.leos.annotate.repository.TokenRepository;
import eu.europa.ec.leos.annotate.repository.UserGroupRepository;
import eu.europa.ec.leos.annotate.repository.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.config.name=anot")
@WebAppConfiguration
@ActiveProfiles("test")
public class RefreshTokenExpiredTest {

    /**
     * Test that request using expired refresh token is refused - run test using a profile update request
     */
    private static final String ACCESS_TOKEN = "demoaccesstoken", REFRESH_TOKEN = "noLongerRefreshing";

    // -------------------------------------
    // Required services and repositories
    // -------------------------------------
    @Autowired
    private GroupRepository groupRepos;

    @Autowired
    private UserRepository userRepos;

    @Autowired
    private UserGroupRepository userGroupRepos;

    @Autowired
    private TokenRepository tokenRepos;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private Group defaultGroup;

    // -------------------------------------
    // Cleanup of database content
    // -------------------------------------
    @Before
    public void setupTests() {

        MockitoAnnotations.initMocks(this);

        TestDbHelper.cleanupRepositories(this);
        defaultGroup = TestDbHelper.insertDefaultGroup(groupRepos);

        final DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @After
    public void cleanDatabaseAfterTests() {

        TestDbHelper.cleanupRepositories(this);
    }

    // -------------------------------------
    // Tests
    // -------------------------------------

    /**
     * sending a request for getting a new refresh token using an expired refresh token, expected HTTP 400 and failure response
     */
    @Test
    @SuppressFBWarnings(value = SpotBugsAnnotations.FieldNotInitialized, justification = SpotBugsAnnotations.FieldNotInitializedReason)
    public void testExpiredRefreshTokenIsRefused() throws Exception {

        final String login = "demo";

        // preparation: save a user and assign it to a group
        final User theUser = new User(login);
        userRepos.save(theUser);
        tokenRepos.save(new Token(theUser, "auth", ACCESS_TOKEN, LocalDateTime.now().plusMinutes(1), REFRESH_TOKEN,
                LocalDateTime.now().minusMinutes(1))); // refresh token expired

        final UserGroup membership = new UserGroup();
        membership.setUserId(theUser.getId());
        membership.setGroupId(defaultGroup.getId());
        userGroupRepos.save(membership);

        // send refresh token request
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/token?grant_type=refresh_token&refresh_token=" + REFRESH_TOKEN);

        final ResultActions result = this.mockMvc.perform(builder);

        // expected: Http 400 - cf. https://docs.apigee.com/api-platform/reference/policies/oauth-http-status-code-reference
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());

        final MvcResult resultContent = result.andReturn();
        final String responseString = resultContent.getResponse().getContentAsString();

        // check that the expected annotation was returned
        final JsonAuthenticationFailure jsResponse = SerialisationHelper.deserializeJsonAuthenticationFailure(responseString);
        Assert.assertNotNull(jsResponse);

        // invalid token response expected
        Assert.assertEquals("invalid_grant", jsResponse.getError());
        Assert.assertTrue(!jsResponse.getError_description().isEmpty());
    }

    /**
     * sending a request for getting a new refresh token, but not providing the previous refresh token, expected HTTP 400 and failure response
     */
    @Test
    @SuppressFBWarnings(value = SpotBugsAnnotations.FieldNotInitialized, justification = SpotBugsAnnotations.FieldNotInitializedReason)
    public void testRefreshTokenNotSent() throws Exception {

        final String login = "demo";

        // preparation: save a user and assign it to a group
        final User theUser = new User(login);
        userRepos.save(theUser);
        tokenRepos.save(new Token(theUser, "auth", ACCESS_TOKEN, LocalDateTime.now().plusMinutes(1), REFRESH_TOKEN,
                LocalDateTime.now().minusMinutes(1))); // refresh token expired

        final UserGroup membership = new UserGroup();
        membership.setUserId(theUser.getId());
        membership.setGroupId(defaultGroup.getId());
        userGroupRepos.save(membership);

        // send refresh token request
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/token?grant_type=refresh_token&refresh_token=");

        final ResultActions result = this.mockMvc.perform(builder);

        // expected: Http 400 - cf. https://docs.apigee.com/api-platform/reference/policies/oauth-http-status-code-reference
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());

        final MvcResult resultContent = result.andReturn();
        final String responseString = resultContent.getResponse().getContentAsString();

        // check that the expected annotation was returned
        final JsonAuthenticationFailure jsResponse = SerialisationHelper.deserializeJsonAuthenticationFailure(responseString);
        Assert.assertNotNull(jsResponse);

        // invalid token response expected
        Assert.assertEquals("invalid_grant", jsResponse.getError());
        Assert.assertTrue(!jsResponse.getError_description().isEmpty());
    }
}
