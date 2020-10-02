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
import eu.europa.ec.leos.annotate.controllers.GroupApiController;
import eu.europa.ec.leos.annotate.helper.SerialisationHelper;
import eu.europa.ec.leos.annotate.helper.SpotBugsAnnotations;
import eu.europa.ec.leos.annotate.model.web.JsonFailureResponse;
import eu.europa.ec.leos.annotate.model.web.user.JsonGroupWithDetails;
import eu.europa.ec.leos.annotate.services.AuthenticationService;
import eu.europa.ec.leos.annotate.services.GroupService;
import eu.europa.ec.leos.annotate.services.impl.AuthenticatedUserStore;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.util.StringUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.config.name=anot")
@WebAppConfiguration
@ActiveProfiles("test")
public class GroupsWithMockTest {

    // -------------------------------------
    // Required services and repositories
    // -------------------------------------

    // the GroupService is mocked
    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupApiController groupController;

    // needs to be mocked also in order to be available at all
    @Mock
    private AuthenticatedUserStore authStore;
    @Mock
    private AuthenticationService authService;

    private MockMvc mockMvc;

    // -------------------------------------
    // Cleanup of database content
    // -------------------------------------
    @Before
    public void setupTests() {

        MockitoAnnotations.initMocks(this);

        final StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(groupController);
        this.mockMvc = builder.build();
    }

    // -------------------------------------
    // Tests
    // -------------------------------------
    /**
     * check that at least an empty list is returned in case the GroupService has problems, expected HTTP 200
     */
    @Test
    @SuppressFBWarnings(value = {SpotBugsAnnotations.FieldNotInitialized,
            "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"}, justification = "Initialisation is done in function that is run before each test; passing null is done for testing purposes")
    public void testGetGroupsGetsNull() throws Exception {

        final String authority = "myauthority";

        Mockito.when(groupService.getUserGroupsAsJson(null)).thenReturn(null);

        // send group retrieval request - without authorization header
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/groups?authority=" + authority + "&expand=ignore&document_uri=ignore");
        Mockito.when(authStore.getUserInfo()).thenReturn(null);
        Mockito.when(authService.getUserLogin(null)).thenReturn(null);

        final ResultActions result = this.mockMvc.perform(builder);

        // expected: Http 200
        result.andExpect(MockMvcResultMatchers.status().isOk());

        final MvcResult resultContent = result.andReturn();
        final String responseString = resultContent.getResponse().getContentAsString();

        // check that the empty group list was returned
        final List<JsonGroupWithDetails> jsResponse = SerialisationHelper.deserializeJsonGroupWithDetails(responseString);
        Assert.assertNotNull(jsResponse);
        Assert.assertEquals(0, jsResponse.size());
    }

    /**
     * provoke error in /groups request by making a service throw an exception; expect HTTP 400
     */
    @Test
    @SuppressFBWarnings(value = SpotBugsAnnotations.FieldNotInitialized, justification = SpotBugsAnnotations.FieldNotInitializedReason)
    public void testGetGroupsThrowsExceptions() throws Exception {

        final String authority = "myauthority";

        // send group retrieval request - without authorization header
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/groups?authority=" + authority + "&expand=ignore&document_uri=ignore");

        // we throw any exception to provoke running through catch block of controller method
        Mockito.when(groupService.getUserGroupsAsJson(null)).thenThrow(new IllegalArgumentException());

        final ResultActions result = this.mockMvc.perform(builder);

        // expected: Http 400
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());

        final MvcResult resultContent = result.andReturn();
        final String responseString = resultContent.getResponse().getContentAsString();

        // check that the empty group list was returned
        final JsonFailureResponse jsResponse = SerialisationHelper.deserializeJsonFailureResponse(responseString);
        Assert.assertNotNull(jsResponse);
        Assert.assertFalse(StringUtils.isEmpty(jsResponse.getReason()));
    }
}
