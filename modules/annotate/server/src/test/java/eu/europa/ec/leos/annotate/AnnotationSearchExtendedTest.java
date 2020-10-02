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
package eu.europa.ec.leos.annotate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.europa.ec.leos.annotate.helper.SpotBugsAnnotations;
import eu.europa.ec.leos.annotate.helper.TestDbHelper;
import eu.europa.ec.leos.annotate.model.search.AnnotationSearchOptions;
import eu.europa.ec.leos.annotate.model.search.AnnotationSearchResult;
import eu.europa.ec.leos.annotate.model.UserInformation;
import eu.europa.ec.leos.annotate.model.entity.*;
import eu.europa.ec.leos.annotate.model.web.annotation.JsonAnnotation;
import eu.europa.ec.leos.annotate.model.web.annotation.JsonSearchResult;
import eu.europa.ec.leos.annotate.model.web.annotation.JsonSearchResultWithSeparateReplies;
import eu.europa.ec.leos.annotate.repository.*;
import eu.europa.ec.leos.annotate.services.AnnotationConversionService;
import eu.europa.ec.leos.annotate.services.AnnotationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.config.name=anot")
@ActiveProfiles("test")
public class AnnotationSearchExtendedTest {

    /**
     * Tests on search functionality for annotations - extended sample data taken from our test database
     * (i.e. data set with which we observed unexpected results)
     */

    // -------------------------------------
    // Required services and repositories
    // -------------------------------------
    @Autowired
    private GroupRepository groupRepos;

    @Autowired
    @Qualifier("annotationTestRepos")
    private AnnotationTestRepository annotRepos;

    @Autowired
    private AnnotationConversionService conversionService;
    
    @Autowired
    private UserRepository userRepos;

    @Autowired
    private UserGroupRepository userGroupRepos;

    @Autowired
    private DocumentRepository documentRepos;

    @Autowired
    private MetadataRepository metadataRepos;

    @Autowired
    private AnnotationService annotService;

    // -------------------------------------
    // Cleanup of database content and prepare test data
    // -------------------------------------
    private static final String DOC_URL = "uri://LEOS/doc1";
    private static final String USER_LOGIN = "userLogin";
    private static final String AUTHORITY = Authorities.EdiT;
    private static final String WORLDGROUP = "__world__";
    private static final String ASC = "asc";
    private static final String CREATED = "created";
    
    private UserInformation userInfo;

    private static final String ID_USER1_1_PUB = "dDx4SEAjR-C4TR0J8s9IaQ";
    private static final String ID_USER1_2_PUB = "E4-LjixbS0CwNEPu9EDfjA";
    private static final String ID_USER1_3_PUB = "cUkva_1MSVaLXGIHOEVBHA";
    private static final String ID_USER1_4_PUB = "imMyuSbeSoetO2-_cMfnrQ";
    private static final String ID_USER1_5_PRIV = "gD8BHOs9R--PHDygxS4prg";
    private static final String ID_USER1_6_PRIV = "SMEMXl4gSbW0j1Gj3SLQ3w";
    private static final String ID_USER1_7_PRIV = "8Z16HBetT26H6NybYaI7Yg";
    private static final String ID_USER1_8_PRIV = "IaozutxkRUq8Nthrz1EmvQ";
    private static final String ID_USER1_9_PRIV = "AeAJ6jX1RjOv4OpOfFEZdw";
    private static final String ID_USER1_10_PUB = "jVtHuWFsQiOGD8Vg0GMnVQ";
    private static final String ID_USER1_11_PUB = "x8USFnmzTwaxBFFI02uKWA";
    private static final String ID_USER1_12_PUB = "cB_aTYVCTfKkwKknBndUeQ";
    private static final String ID_USER1_13_PUB = "CToFE33XQiOT0KdemkBeLA";
    private static final String ID_USER1_14_PUB = "kQi4OwhBSFKUe9zu9NvTAA";
    private static final String ID_USER1_15_PUB = "6LUUKoRoQWqcFTTwEAcRaQ";
    private static final String ID_USER1_16_PUB = "yVhIPm4sT7S8udmfyEvLTg";
    private static final String ID_USER1_17_PUB = "RB0WNYljQ2meOre2WFrzpw";
    private static final String ID_USER1_18_PUB = "4rzazIXwQUK2kYkYGwonAw";
    private static final String ID_USER1_19_PUB_REP = "XVz5ED6cTNOyLDZNLc-eOg";
    private static final String ID_USER1_19_PUB_REP_DEL = "DFUah4X2SHahcu6bjgQ8Aw";
    private static final String ID_USER1_20_PUB = "VX12QSnGQuiHCTiLGfbikA";
    private static final String ID_USER1_21_PUB_REPLY = "WFCffWcLQYuUYojZuBkWrg";
    private static final String ID_USER1_22_PUB_REPLY = "6SzIQC-CS5GV9NkCCvnPUQ";
    private static final String ID_USER1_23_PUB_REPLY = "_N6ZA5v0SC2vHz5BGIxWKA";
    private static final String ID_USER1_24_PRIV = "FnAOJPctQzS7AdarWxmVqQ";
    private static final String ID_USER1_25_PRIV = "cpXc7X_JSnC7WXAU_ZMPsQ";
    private static final String ID_USER2_26_PUB = "Zi6d7Q1fQd2-RZFUlmsdWg";
    private static final String ID_USER1_27_PRIV_PNT = "rOLKWGlRQrmWgFPXA18_Ww";
    private static final String ID_USER1_28_PUB_REP_PNT = "fpvfFb83RNewptNHxXgYiw";
    private static final String ID_USER2_29_PRIV_HLT = "qxwnazYgQ3-oYLzRaOCz0w";
    private static final String ID_USER1_30_PUB_PNT = "SdAx2AeHQE2NBMjcIcV5DA";
    private static final String ID_USER1_31_PUB = "2NTBWRxhTM2EkhzId_r1aA";
    private static final String ID_USER1_32_PUB = "toDnbyCSRaSiW5aY6O9Jgw";
    private static final String ID_USER1_33_PRIV = "H8yF15JFSxWcjhMCljMqOg";
    private static final String ID_USER3_34_PUB = "1NMs9Xc9Tmiui9rHDi7c6A";
    private static final String ID_USER3_35_PUB_REPLY = "OtWIOBKyRMeBm9oOrMzatA";
    private static final String ID_USER3_36_PUB = "TL_s-VviQeecIgEdStr3qg";

    @Before
    @SuppressWarnings("PMD.ExcessiveMethodLength") // because its test data creation; no harm in here
    public void cleanDatabaseBeforeTests() throws URISyntaxException {

        TestDbHelper.cleanupRepositories(this);
        final Group defaultGroup = TestDbHelper.insertDefaultGroup(groupRepos);

        // insert user, assign to the default group
        final User firstUser = new User(USER_LOGIN);
        final User secondUser = new User("secondUser");
        final User thirdUser = new User("thirdUser");
        userRepos.save(Arrays.asList(firstUser, secondUser, thirdUser));
        userGroupRepos.save(new UserGroup(firstUser.getId(), defaultGroup.getId()));
        userGroupRepos.save(new UserGroup(secondUser.getId(), defaultGroup.getId()));
        userGroupRepos.save(new UserGroup(thirdUser.getId(), defaultGroup.getId()));

        // create user info here with reference to the firstUser object! (has cost hours of debugging and searching...)
        userInfo = new UserInformation(new Token(firstUser, AUTHORITY, "access", LocalDateTime.now().plusHours(1), "refr",LocalDateTime.now().plusHours(1)));
        
        // insert a document and metadata
        final Document firstDoc = new Document(new URI(DOC_URL), "document's title");
        documentRepos.save(firstDoc);

        final Metadata meta = new Metadata(firstDoc, defaultGroup, AUTHORITY);
        metadataRepos.save(meta);

        // insert annotations; following structure (annotations/replies)
        // 1
        // ...
        // 17
        // 18
        // └ deleted reply
        // └ 19
        // 20
        // └ 21
        // | └ 23
        // └ 22
        // 24
        // 25
        // 26
        // └ 35
        // 27
        // ...
        // 34
        // 36

        // dummy selector
        final String dummySelector = "[{\"selector\":null,\"source\":\"" + firstDoc.getUri() + "\"}]";

        // first public annotation of first user
        final Annotation firstAnnot = new Annotation();
        firstAnnot.setId(ID_USER1_1_PUB);
        firstAnnot.setCreated(LocalDateTime.of(2017, 12, 22, 11, 40, 57));
        firstAnnot.setUpdated(LocalDateTime.of(2017, 12, 22, 11, 42, 59));
        firstAnnot.setMetadata(meta);
        firstAnnot.setReferences("");
        firstAnnot.setShared(true);
        firstAnnot.setTargetSelectors(dummySelector);
        firstAnnot.setText("www");
        firstAnnot.setUser(firstUser);
        annotRepos.save(firstAnnot);

        // second public annotation of first user
        final Annotation secondAnnot = new Annotation();
        secondAnnot.setId(ID_USER1_2_PUB);
        secondAnnot.setCreated(LocalDateTime.of(2017, 12, 22, 12, 11, 28));
        secondAnnot.setUpdated(LocalDateTime.of(2017, 12, 22, 12, 11, 46));
        secondAnnot.setMetadata(meta);
        secondAnnot.setReferences("");
        secondAnnot.setShared(true);
        secondAnnot.setTargetSelectors(dummySelector);
        secondAnnot.setText("rrr");
        secondAnnot.setUser(firstUser);
        annotRepos.save(secondAnnot);

        // third public annotation of first user
        final Annotation thirdAnnot = new Annotation();
        thirdAnnot.setId(ID_USER1_3_PUB);
        thirdAnnot.setCreated(LocalDateTime.of(2017, 12, 22, 12, 11, 29));
        thirdAnnot.setUpdated(LocalDateTime.of(2017, 12, 22, 12, 12, 33));
        thirdAnnot.setMetadata(meta);
        thirdAnnot.setReferences("");
        thirdAnnot.setShared(true);
        thirdAnnot.setTargetSelectors(dummySelector);
        thirdAnnot.setText("rrr");
        thirdAnnot.setUser(firstUser);
        annotRepos.save(thirdAnnot);

        // fourth public annotation of first user
        final Annotation fourthAnnot = new Annotation();
        fourthAnnot.setId(ID_USER1_4_PUB);
        fourthAnnot.setCreated(LocalDateTime.of(2017, 12, 22, 13, 00, 57));
        fourthAnnot.setUpdated(LocalDateTime.of(2017, 12, 22, 13, 01, 49));
        fourthAnnot.setMetadata(meta);
        fourthAnnot.setReferences("");
        fourthAnnot.setShared(true);
        fourthAnnot.setTargetSelectors(dummySelector);
        fourthAnnot.setText("ssssssssssssssssddddddddddddd");
        fourthAnnot.setUser(firstUser);
        annotRepos.save(fourthAnnot);

        // fifth annotation of first user: private!
        final Annotation fifthAnnot = new Annotation();
        fifthAnnot.setId(ID_USER1_5_PRIV);
        fifthAnnot.setCreated(LocalDateTime.of(2017, 12, 22, 13, 01, 56));
        fifthAnnot.setUpdated(LocalDateTime.of(2017, 12, 22, 13, 02, 15));
        fifthAnnot.setMetadata(meta);
        fifthAnnot.setReferences("");
        fifthAnnot.setShared(false);
        fifthAnnot.setTargetSelectors(dummySelector);
        fifthAnnot.setText("publ");
        fifthAnnot.setUser(firstUser);
        annotRepos.save(fifthAnnot);

        // sixth annotation of first user: private!
        final Annotation sixthAnnot = new Annotation();
        sixthAnnot.setId(ID_USER1_6_PRIV);
        sixthAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 8, 27, 55));
        sixthAnnot.setUpdated(LocalDateTime.of(2017, 12, 27, 8, 28, 38));
        sixthAnnot.setMetadata(meta);
        sixthAnnot.setReferences("");
        sixthAnnot.setShared(false);
        sixthAnnot.setTargetSelectors(dummySelector);
        sixthAnnot.setText("qas");
        sixthAnnot.setUser(firstUser);
        annotRepos.save(sixthAnnot);

        // seventh annotation of first user: private!
        final Annotation seventhAnnot = new Annotation();
        seventhAnnot.setId(ID_USER1_7_PRIV);
        seventhAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 8, 54, 40));
        seventhAnnot.setUpdated(LocalDateTime.of(2017, 12, 27, 8, 55, 17));
        seventhAnnot.setMetadata(meta);
        seventhAnnot.setReferences("");
        seventhAnnot.setShared(false);
        seventhAnnot.setTargetSelectors(dummySelector);
        seventhAnnot.setText("abcde");
        seventhAnnot.setUser(firstUser);
        annotRepos.save(seventhAnnot);

        // eighth annotation of first user: private!
        final Annotation eighthAnnot = new Annotation();
        eighthAnnot.setId(ID_USER1_8_PRIV);
        eighthAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 9, 11, 21));
        eighthAnnot.setUpdated(LocalDateTime.of(2017, 12, 27, 9, 11, 35));
        eighthAnnot.setMetadata(meta);
        eighthAnnot.setReferences("");
        eighthAnnot.setShared(false);
        eighthAnnot.setTargetSelectors(dummySelector);
        eighthAnnot.setText("bcdef");
        eighthAnnot.setUser(firstUser);
        annotRepos.save(eighthAnnot);

        // ninth annotation of first user: private!
        final Annotation ninthAnnot = new Annotation();
        ninthAnnot.setId(ID_USER1_9_PRIV);
        ninthAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 9, 12, 23));
        ninthAnnot.setUpdated(LocalDateTime.of(2017, 12, 27, 9, 13, 37));
        ninthAnnot.setMetadata(meta);
        ninthAnnot.setReferences("");
        ninthAnnot.setShared(false);
        ninthAnnot.setTargetSelectors(dummySelector);
        ninthAnnot.setText("cdefg");
        ninthAnnot.setUser(firstUser);
        annotRepos.save(ninthAnnot);

        // tenth annotation of first user: public!
        final Annotation tenthAnnot = new Annotation();
        tenthAnnot.setId(ID_USER1_10_PUB);
        tenthAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 9, 44, 39));
        tenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 27, 9, 45, 06));
        tenthAnnot.setMetadata(meta);
        tenthAnnot.setReferences("");
        tenthAnnot.setShared(true);
        tenthAnnot.setTargetSelectors(dummySelector);
        tenthAnnot.setText("tagtest");
        tenthAnnot.setUser(firstUser);
        annotRepos.save(tenthAnnot);

        // eleventh annotation of first user: public!
        final Annotation eleventhAnnot = new Annotation();
        eleventhAnnot.setId(ID_USER1_11_PUB);
        eleventhAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 10, 52, 36));
        eleventhAnnot.setUpdated(LocalDateTime.of(2018, 01, 18, 16, 49, 20));
        eleventhAnnot.setMetadata(meta);
        eleventhAnnot.setReferences("");
        eleventhAnnot.setShared(true);
        eleventhAnnot.setTargetSelectors(dummySelector);
        eleventhAnnot.setText("reg! changed");
        eleventhAnnot.setUser(firstUser);
        annotRepos.save(eleventhAnnot);

        // twelfth annotation of first user: public!
        final Annotation twelfthAnnot = new Annotation();
        twelfthAnnot.setId(ID_USER1_12_PUB);
        twelfthAnnot.setCreated(LocalDateTime.of(2017, 12, 27, 12, 28, 14));
        twelfthAnnot.setUpdated(LocalDateTime.of(2017, 12, 27, 12, 28, 52));
        twelfthAnnot.setMetadata(meta);
        twelfthAnnot.setReferences("");
        twelfthAnnot.setShared(true);
        twelfthAnnot.setTargetSelectors(dummySelector);
        twelfthAnnot.setText("viel *Text*");
        twelfthAnnot.setUser(firstUser);
        annotRepos.save(twelfthAnnot);

        // thirteenth annotation of first user: public!
        final Annotation thirteenthAnnot = new Annotation();
        thirteenthAnnot.setId(ID_USER1_13_PUB);
        thirteenthAnnot.setCreated(LocalDateTime.of(2017, 12, 28, 9, 49, 43));
        thirteenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 28, 9, 49, 51));
        thirteenthAnnot.setMetadata(meta);
        thirteenthAnnot.setReferences("");
        thirteenthAnnot.setShared(true);
        thirteenthAnnot.setTargetSelectors(dummySelector);
        thirteenthAnnot.setText("new annot");
        thirteenthAnnot.setUser(firstUser);
        annotRepos.save(thirteenthAnnot);

        // fourteenth annotation of first user: public!
        final Annotation fourteenthAnnot = new Annotation();
        fourteenthAnnot.setId(ID_USER1_14_PUB);
        fourteenthAnnot.setCreated(LocalDateTime.of(2017, 12, 28, 10, 42, 07));
        fourteenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 28, 10, 42, 16));
        fourteenthAnnot.setMetadata(meta);
        fourteenthAnnot.setReferences("");
        fourteenthAnnot.setShared(true);
        fourteenthAnnot.setTargetSelectors(dummySelector);
        fourteenthAnnot.setText("win10");
        fourteenthAnnot.setUser(firstUser);
        annotRepos.save(fourteenthAnnot);

        // fifteenth annotation of first user: public!
        final Annotation fifteenthAnnot = new Annotation();
        fifteenthAnnot.setId(ID_USER1_15_PUB);
        fifteenthAnnot.setCreated(LocalDateTime.of(2017, 12, 28, 12, 38, 18));
        fifteenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 28, 12, 40, 36));
        fifteenthAnnot.setMetadata(meta);
        fifteenthAnnot.setReferences("");
        fifteenthAnnot.setShared(true);
        fifteenthAnnot.setTargetSelectors(dummySelector);
        fifteenthAnnot.setText("selectortest");
        fifteenthAnnot.setUser(firstUser);
        annotRepos.save(fifteenthAnnot);

        // sixteenth annotation of first user: public!
        final Annotation sixteenthAnnot = new Annotation();
        sixteenthAnnot.setId(ID_USER1_16_PUB);
        sixteenthAnnot.setCreated(LocalDateTime.of(2017, 12, 28, 15, 05, 21));
        sixteenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 28, 15, 05, 41));
        sixteenthAnnot.setMetadata(meta);
        sixteenthAnnot.setReferences("");
        sixteenthAnnot.setShared(true);
        sixteenthAnnot.setTargetSelectors(dummySelector);
        sixteenthAnnot.setText("text on text");
        sixteenthAnnot.setUser(firstUser);
        annotRepos.save(sixteenthAnnot);

        // seventeenth annotation of first user: public!
        final Annotation seventeenthAnnot = new Annotation();
        seventeenthAnnot.setId(ID_USER1_17_PUB);
        seventeenthAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 10, 42, 20));
        seventeenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 10, 42, 40));
        seventeenthAnnot.setMetadata(meta);
        seventeenthAnnot.setReferences("");
        seventeenthAnnot.setShared(true);
        seventeenthAnnot.setTargetSelectors(dummySelector);
        seventeenthAnnot.setText("new comment");
        seventeenthAnnot.setUser(firstUser);
        annotRepos.save(seventeenthAnnot);

        // eighteenth annotation of first user: public!
        final Annotation eighteenthAnnot = new Annotation();
        eighteenthAnnot.setId(ID_USER1_18_PUB);
        eighteenthAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 10, 50, 42));
        eighteenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 10, 56, 28));
        eighteenthAnnot.setMetadata(meta);
        eighteenthAnnot.setReferences("");
        eighteenthAnnot.setShared(true);
        eighteenthAnnot.setTargetSelectors(dummySelector);
        eighteenthAnnot.setText("parent");
        eighteenthAnnot.setUser(firstUser);
        annotRepos.save(eighteenthAnnot);

        // nineteenth annotation of first user: public reply (2nd degree)!
        final Annotation nineteenthAnnot = new Annotation();
        nineteenthAnnot.setId(ID_USER1_19_PUB_REP);
        nineteenthAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 10, 59, 01));
        nineteenthAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 10, 59, 32));
        nineteenthAnnot.setMetadata(meta);
        nineteenthAnnot.setReferences(Arrays.asList(ID_USER1_18_PUB, ID_USER1_19_PUB_REP_DEL));
        nineteenthAnnot.setShared(true);
        nineteenthAnnot.setTargetSelectors(dummySelector);
        nineteenthAnnot.setText("child of child");
        nineteenthAnnot.setUser(firstUser);
        annotRepos.save(nineteenthAnnot);

        // twentieth annotation of first user: public!
        final Annotation twentiethAnnot = new Annotation();
        twentiethAnnot.setId(ID_USER1_20_PUB);
        twentiethAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 13, 06, 54));
        twentiethAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 13, 13, 19));
        twentiethAnnot.setMetadata(meta);
        twentiethAnnot.setReferences("");
        twentiethAnnot.setShared(true);
        twentiethAnnot.setTargetSelectors(dummySelector);
        twentiethAnnot.setText("new root comment");
        twentiethAnnot.setUser(firstUser);
        annotRepos.save(twentiethAnnot);

        // twentyfirst annotation of first user: public reply!
        final Annotation twentyfirstAnnot = new Annotation();
        twentyfirstAnnot.setId(ID_USER1_21_PUB_REPLY);
        twentyfirstAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 13, 13, 49));
        twentyfirstAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 13, 14, 27));
        twentyfirstAnnot.setMetadata(meta);
        twentyfirstAnnot.setReferences(ID_USER1_20_PUB);
        twentyfirstAnnot.setShared(true);
        twentyfirstAnnot.setTargetSelectors(dummySelector);
        twentyfirstAnnot.setText("first reply to root");
        twentyfirstAnnot.setUser(firstUser);
        annotRepos.save(twentyfirstAnnot);

        // twentysecond annotation of first user: public reply!
        final Annotation twentysecondAnnot = new Annotation();
        twentysecondAnnot.setId(ID_USER1_22_PUB_REPLY);
        twentysecondAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 13, 14, 48));
        twentysecondAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 13, 15, 9));
        twentysecondAnnot.setMetadata(meta);
        twentysecondAnnot.setReferences(ID_USER1_20_PUB);
        twentysecondAnnot.setShared(true);
        twentysecondAnnot.setTargetSelectors(dummySelector);
        twentysecondAnnot.setText("second reply to root");
        twentysecondAnnot.setUser(firstUser);
        annotRepos.save(twentysecondAnnot);

        // twentythird annotation of first user: public reply!
        final Annotation twentythirdAnnot = new Annotation();
        twentythirdAnnot.setId(ID_USER1_23_PUB_REPLY);
        twentythirdAnnot.setCreated(LocalDateTime.of(2017, 12, 29, 13, 15, 28));
        twentythirdAnnot.setUpdated(LocalDateTime.of(2017, 12, 29, 13, 16, 01));
        twentythirdAnnot.setMetadata(meta);
        twentythirdAnnot.setReferences(Arrays.asList(ID_USER1_20_PUB, ID_USER1_21_PUB_REPLY));
        twentythirdAnnot.setShared(true);
        twentythirdAnnot.setTargetSelectors(dummySelector);
        twentythirdAnnot.setText("first reply *to *first reply");
        twentythirdAnnot.setUser(firstUser);
        annotRepos.save(twentythirdAnnot);

        // twentyfourth annotation of first user: private!
        final Annotation twentyfourthAnnot = new Annotation();
        twentyfourthAnnot.setId(ID_USER1_24_PRIV);
        twentyfourthAnnot.setCreated(LocalDateTime.of(2018, 01, 3, 8, 44, 48));
        twentyfourthAnnot.setUpdated(LocalDateTime.of(2018, 01, 3, 8, 45, 05));
        twentyfourthAnnot.setMetadata(meta);
        twentyfourthAnnot.setReferences("");
        twentyfourthAnnot.setShared(false);
        twentyfourthAnnot.setTargetSelectors(dummySelector);
        twentyfourthAnnot.setText("hallo");
        twentyfourthAnnot.setUser(firstUser);
        annotRepos.save(twentyfourthAnnot);

        // twentyfifth annotation of first user: private!
        final Annotation twentyfifthAnnot = new Annotation();
        twentyfifthAnnot.setId(ID_USER1_25_PRIV);
        twentyfifthAnnot.setCreated(LocalDateTime.of(2018, 01, 9, 14, 9, 20));
        twentyfifthAnnot.setUpdated(LocalDateTime.of(2018, 01, 10, 12, 52, 28));
        twentyfifthAnnot.setMetadata(meta);
        twentyfifthAnnot.setReferences("");
        twentyfifthAnnot.setShared(false);
        twentyfifthAnnot.setTargetSelectors(dummySelector);
        twentyfifthAnnot.setText("a new annotation");
        twentyfifthAnnot.setUser(firstUser);
        annotRepos.save(twentyfifthAnnot);

        // twentysixth annotation of second user: private!
        final Annotation twentysixthAnnot = new Annotation();
        twentysixthAnnot.setId(ID_USER2_26_PUB);
        twentysixthAnnot.setCreated(LocalDateTime.of(2018, 01, 10, 12, 58, 00));
        twentysixthAnnot.setUpdated(LocalDateTime.of(2018, 01, 11, 10, 18, 53));
        twentysixthAnnot.setMetadata(meta);
        twentysixthAnnot.setReferences("");
        twentysixthAnnot.setShared(true);
        twentysixthAnnot.setTargetSelectors(dummySelector);
        twentysixthAnnot.setText("my ann2aAA");
        twentysixthAnnot.setUser(secondUser);
        annotRepos.save(twentysixthAnnot);

        // twentyseventh annotation of first user: private page note!
        final Annotation twentyseventhAnnot = new Annotation();
        twentyseventhAnnot.setId(ID_USER1_27_PRIV_PNT);
        twentyseventhAnnot.setCreated(LocalDateTime.of(2018, 01, 11, 11, 30, 05));
        twentyseventhAnnot.setUpdated(LocalDateTime.of(2018, 01, 11, 11, 30, 59));
        twentyseventhAnnot.setMetadata(meta);
        twentyseventhAnnot.setReferences("");
        twentyseventhAnnot.setShared(false);
        twentyseventhAnnot.setTargetSelectors(dummySelector);
        twentyseventhAnnot.setText("This is my _updated_ page note");
        twentyseventhAnnot.setUser(firstUser);
        annotRepos.save(twentyseventhAnnot);

        // twentyeighth annotation of first user: public reply to private page note!
        final Annotation twentyeighthAnnot = new Annotation();
        twentyeighthAnnot.setId(ID_USER1_28_PUB_REP_PNT);
        twentyeighthAnnot.setCreated(LocalDateTime.of(2018, 01, 11, 11, 31, 25));
        twentyeighthAnnot.setUpdated(LocalDateTime.of(2018, 01, 11, 11, 31, 50));
        twentyeighthAnnot.setMetadata(meta);
        twentyeighthAnnot.setReferences(ID_USER1_27_PRIV_PNT);
        twentyeighthAnnot.setShared(true);
        twentyeighthAnnot.setTargetSelectors(dummySelector);
        twentyeighthAnnot.setText("reply to page note (public)");
        twentyeighthAnnot.setUser(firstUser);
        annotRepos.save(twentyeighthAnnot);

        // twentyninth annotation of second user: private highlight!
        final Annotation twentyninthAnnot = new Annotation();
        twentyninthAnnot.setId(ID_USER2_29_PRIV_HLT);
        twentyninthAnnot.setCreated(LocalDateTime.of(2018, 01, 11, 11, 32, 25));
        twentyninthAnnot.setUpdated(LocalDateTime.of(2018, 01, 11, 11, 32, 25));
        twentyninthAnnot.setMetadata(meta);
        twentyninthAnnot.setReferences("");
        twentyninthAnnot.setShared(false);
        twentyninthAnnot.setTargetSelectors(dummySelector);
        twentyninthAnnot.setText(null);
        twentyninthAnnot.setUser(secondUser);
        annotRepos.save(twentyninthAnnot);

        // thirtyth annotation of first user: public page note!
        final Annotation thirtythAnnot = new Annotation();
        thirtythAnnot.setId(ID_USER1_30_PUB_PNT);
        thirtythAnnot.setCreated(LocalDateTime.of(2018, 01, 11, 14, 01, 34));
        thirtythAnnot.setUpdated(LocalDateTime.of(2018, 01, 11, 14, 01, 58));
        thirtythAnnot.setMetadata(meta);
        thirtythAnnot.setReferences("");
        thirtythAnnot.setShared(true);
        thirtythAnnot.setTargetSelectors(dummySelector);
        thirtythAnnot.setText("new page note");
        thirtythAnnot.setUser(secondUser);
        annotRepos.save(thirtythAnnot);

        // thirtyfirst annotation of first user: public
        final Annotation thirtyfirstAnnot = new Annotation();
        thirtyfirstAnnot.setId(ID_USER1_31_PUB);
        thirtyfirstAnnot.setCreated(LocalDateTime.of(2018, 01, 11, 16, 05, 23));
        thirtyfirstAnnot.setUpdated(LocalDateTime.of(2018, 01, 11, 16, 05, 42));
        thirtyfirstAnnot.setMetadata(meta);
        thirtyfirstAnnot.setReferences("");
        thirtyfirstAnnot.setShared(true);
        thirtyfirstAnnot.setTargetSelectors(dummySelector);
        thirtyfirstAnnot.setText("test new annot");
        thirtyfirstAnnot.setUser(secondUser);
        annotRepos.save(thirtyfirstAnnot);

        // thirtysecond annotation of first user: public
        final Annotation thirtysecondAnnot = new Annotation();
        thirtysecondAnnot.setId(ID_USER1_32_PUB);
        thirtysecondAnnot.setCreated(LocalDateTime.of(2018, 01, 24, 18, 50, 06));
        thirtysecondAnnot.setUpdated(LocalDateTime.of(2018, 01, 24, 18, 50, 11));
        thirtysecondAnnot.setMetadata(meta);
        thirtysecondAnnot.setReferences("");
        thirtysecondAnnot.setShared(true);
        thirtysecondAnnot.setTargetSelectors(dummySelector);
        thirtysecondAnnot.setText("XZCdfsa");
        thirtysecondAnnot.setUser(secondUser);
        annotRepos.save(thirtysecondAnnot);

        // thirtythird annotation of first user: private
        final Annotation thirtythirdAnnot = new Annotation();
        thirtythirdAnnot.setId(ID_USER1_33_PRIV);
        thirtythirdAnnot.setCreated(LocalDateTime.of(2018, 01, 25, 18, 38, 03));
        thirtythirdAnnot.setUpdated(LocalDateTime.of(2018, 01, 25, 18, 38, 06));
        thirtythirdAnnot.setMetadata(meta);
        thirtythirdAnnot.setReferences("");
        thirtythirdAnnot.setShared(false);
        thirtythirdAnnot.setTargetSelectors(dummySelector);
        thirtythirdAnnot.setText("new ann");
        thirtythirdAnnot.setUser(firstUser);
        annotRepos.save(thirtythirdAnnot);

        // thirtyfourth annotation of third user: public
        final Annotation thirtyfourthAnnot = new Annotation();
        thirtyfourthAnnot.setId(ID_USER3_34_PUB);
        thirtyfourthAnnot.setCreated(LocalDateTime.of(2018, 01, 26, 9, 35, 19));
        thirtyfourthAnnot.setUpdated(LocalDateTime.of(2018, 01, 26, 9, 35, 23));
        thirtyfourthAnnot.setMetadata(meta);
        thirtyfourthAnnot.setReferences("");
        thirtyfourthAnnot.setShared(true);
        thirtyfourthAnnot.setTargetSelectors(dummySelector);
        thirtyfourthAnnot.setText("darth2");
        thirtyfourthAnnot.setUser(thirdUser);
        annotRepos.save(thirtyfourthAnnot);

        // thirtyfifth annotation of third user: public reply
        final Annotation thirtyfifthAnnot = new Annotation();
        thirtyfifthAnnot.setId(ID_USER3_35_PUB_REPLY);
        thirtyfifthAnnot.setCreated(LocalDateTime.of(2018, 01, 26, 10, 01, 36));
        thirtyfifthAnnot.setUpdated(LocalDateTime.of(2018, 01, 26, 10, 01, 41));
        thirtyfifthAnnot.setMetadata(meta);
        thirtyfifthAnnot.setReferences(ID_USER2_26_PUB);
        thirtyfifthAnnot.setShared(true);
        thirtyfifthAnnot.setTargetSelectors(dummySelector);
        thirtyfifthAnnot.setText("reply darth");
        thirtyfifthAnnot.setUser(thirdUser);
        annotRepos.save(thirtyfifthAnnot);

        // thirtysixth annotation of third user: public
        final Annotation thirtysixthAnnot = new Annotation();
        thirtysixthAnnot.setId(ID_USER3_36_PUB);
        thirtysixthAnnot.setCreated(LocalDateTime.of(2018, 01, 26, 10, 04, 53));
        thirtysixthAnnot.setUpdated(LocalDateTime.of(2018, 01, 26, 10, 05, 01));
        thirtysixthAnnot.setMetadata(meta);
        thirtysixthAnnot.setReferences("");
        thirtysixthAnnot.setShared(true);
        thirtysixthAnnot.setTargetSelectors(dummySelector);
        thirtysixthAnnot.setText("updated!");
        thirtysixthAnnot.setUser(thirdUser);
        annotRepos.save(thirtysixthAnnot);
    }

    @After
    public void cleanDatabaseAfterTests() {

        TestDbHelper.cleanupRepositories(this);
    }

    // -------------------------------------
    // Tests
    // -------------------------------------

    /**
     * test: get first slice of 10 items, check correct received items and replies by their IDs
     */
    @SuppressFBWarnings(value = SpotBugsAnnotations.FieldNotInitialized, justification = SpotBugsAnnotations.FieldNotInitializedReason)
    @Test
    public void testFirstBunchOfTenItems() {

        // retrieve first 10 annotations
        final AnnotationSearchOptions options = new AnnotationSearchOptions(
                DOC_URL, WORLDGROUP,  // URI, group
                true,                 // provide separate replies
                10, 0,                // limit, offset
                ASC, CREATED);        // order, sort column

        final AnnotationSearchResult annotations = annotService.searchAnnotations(options, userInfo);
        Assert.assertEquals(10, annotations.size());
        Assert.assertEquals(29, annotations.getTotalItems());

        final List<Annotation> replies = annotService.searchRepliesForAnnotations(annotations, options, userInfo);
        Assert.assertEquals(0, replies.size());

        final JsonSearchResult initialResult = conversionService.convertToJsonSearchResult(annotations, replies, options, userInfo);
        Assert.assertTrue(initialResult instanceof JsonSearchResultWithSeparateReplies);
        final JsonSearchResultWithSeparateReplies result = (JsonSearchResultWithSeparateReplies) initialResult;

        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.getRows().size());   // 10 items expected
        Assert.assertEquals(0, result.getReplies().size()); // and 0 replies

        // check that the correct items (in correct order) were returned and check their respective number of replies
        assertItemAndReplies(result.getRows().get(0), ID_USER1_1_PUB, null);
        assertItemAndReplies(result.getRows().get(1), ID_USER1_2_PUB, null);
        assertItemAndReplies(result.getRows().get(2), ID_USER1_3_PUB, null);
        assertItemAndReplies(result.getRows().get(3), ID_USER1_4_PUB, null);
        assertItemAndReplies(result.getRows().get(4), ID_USER1_5_PRIV, null);
        assertItemAndReplies(result.getRows().get(5), ID_USER1_6_PRIV, null);
        assertItemAndReplies(result.getRows().get(6), ID_USER1_7_PRIV, null);
        assertItemAndReplies(result.getRows().get(7), ID_USER1_8_PRIV, null);
        assertItemAndReplies(result.getRows().get(8), ID_USER1_9_PRIV, null);
        assertItemAndReplies(result.getRows().get(9), ID_USER1_10_PUB, null);
    }

    /**
     * test: get second slice of 10 items, check correct received items and replies by their IDs
     */
    @Test
    public void testSecondBunchOfTenItems() {

        // retrieve second 10 annotations
        final AnnotationSearchOptions options = new AnnotationSearchOptions(
                DOC_URL, WORLDGROUP,  // URI, group
                true,                 // provide separate replies
                10, 10,               // limit, offset (10/10 -> second bunch of ten items)
                ASC, CREATED);        // order, sort column

        final AnnotationSearchResult annots = annotService.searchAnnotations(options, userInfo);
        Assert.assertEquals(10, annots.size());
        Assert.assertEquals(29, annots.getTotalItems());

        final List<Annotation> replies = annotService.searchRepliesForAnnotations(annots, options, userInfo);
        Assert.assertEquals(4, replies.size());

        final JsonSearchResult initialResult = conversionService.convertToJsonSearchResult(annots, replies, options, userInfo);
        Assert.assertTrue(initialResult instanceof JsonSearchResultWithSeparateReplies);

        final JsonSearchResultWithSeparateReplies result = (JsonSearchResultWithSeparateReplies) initialResult;
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.getRows().size());   // 10 items expected
        Assert.assertEquals(4, result.getReplies().size()); // and 4 replies

        // check that the correct items (in correct order) were returned and check their respective number of replies
        assertItemAndReplies(result.getRows().get(0), ID_USER1_11_PUB, null);
        assertItemAndReplies(result.getRows().get(1), ID_USER1_12_PUB, null);
        assertItemAndReplies(result.getRows().get(2), ID_USER1_13_PUB, null);
        assertItemAndReplies(result.getRows().get(3), ID_USER1_14_PUB, null);
        assertItemAndReplies(result.getRows().get(4), ID_USER1_15_PUB, null);
        assertItemAndReplies(result.getRows().get(5), ID_USER1_16_PUB, null);
        assertItemAndReplies(result.getRows().get(6), ID_USER1_17_PUB, null);
        assertItemAndReplies(result.getRows().get(7), ID_USER1_18_PUB, null);
        // 19 skipped, is a reply
        assertItemAndReplies(result.getRows().get(8), ID_USER1_20_PUB, null);
        // 21-23 skipped, are replies
        assertItemAndReplies(result.getRows().get(9), ID_USER1_24_PRIV, null);

        // same for the replies
        assertItemAndReplies(result.getReplies().get(0), ID_USER1_19_PUB_REP,
                Arrays.asList(ID_USER1_18_PUB, ID_USER1_19_PUB_REP_DEL));
        assertItemAndReplies(result.getReplies().get(1), ID_USER1_21_PUB_REPLY, Arrays.asList(ID_USER1_20_PUB));
        assertItemAndReplies(result.getReplies().get(2), ID_USER1_22_PUB_REPLY, Arrays.asList(ID_USER1_20_PUB));
        assertItemAndReplies(result.getReplies().get(3), ID_USER1_23_PUB_REPLY,
                Arrays.asList(ID_USER1_20_PUB, ID_USER1_21_PUB_REPLY));
    }

    /**
     * test: get third slice of 10 items, check correct received items and replies by their IDs
     */
    @Test
    public void testThirdBunchOfTenItems() {

        // retrieve third 10 annotations
        final AnnotationSearchOptions options = new AnnotationSearchOptions(
                DOC_URL, WORLDGROUP,  // URI, group
                true,                 // provide separate replies
                10, 20,               // limit, offset (10/20 -> third bunch of ten items)
                ASC, CREATED);        // order, sort column

        final AnnotationSearchResult annots = annotService.searchAnnotations(options, userInfo);
        Assert.assertEquals(9, annots.size());
        Assert.assertEquals(29, annots.getTotalItems());

        final List<Annotation> replies = annotService.searchRepliesForAnnotations(annots, options, userInfo);
        Assert.assertEquals(2, replies.size());

        final JsonSearchResult initialResult = conversionService.convertToJsonSearchResult(annots, replies, options, userInfo);
        Assert.assertTrue(initialResult instanceof JsonSearchResultWithSeparateReplies);

        final JsonSearchResultWithSeparateReplies result = (JsonSearchResultWithSeparateReplies) initialResult;

        Assert.assertNotNull(result);
        Assert.assertEquals(9, result.getRows().size());    // 9 annotations expected
        Assert.assertEquals(2, result.getReplies().size()); // and 1 reply

        // check that the correct items (in correct order) were returned and check their respective number of replies
        assertItemAndReplies(result.getRows().get(0), ID_USER1_25_PRIV, null);
        assertItemAndReplies(result.getRows().get(1), ID_USER2_26_PUB, null);
        assertItemAndReplies(result.getRows().get(2), ID_USER1_27_PRIV_PNT, null);
        // 28 skipped, it is a reply to a page note
        // 29 skipped, it is a private highlight of another user
        assertItemAndReplies(result.getRows().get(3), ID_USER1_30_PUB_PNT, null);
        assertItemAndReplies(result.getRows().get(4), ID_USER1_31_PUB, null);
        assertItemAndReplies(result.getRows().get(5), ID_USER1_32_PUB, null);
        assertItemAndReplies(result.getRows().get(6), ID_USER1_33_PRIV, null);
        assertItemAndReplies(result.getRows().get(7), ID_USER3_34_PUB, null);
        // 35 skipped, is a reply
        assertItemAndReplies(result.getRows().get(8), ID_USER3_36_PUB, null);

        // same for the replies
        assertItemAndReplies(result.getReplies().get(0), ID_USER1_28_PUB_REP_PNT, Arrays.asList(ID_USER1_27_PRIV_PNT));
        assertItemAndReplies(result.getReplies().get(1), ID_USER3_35_PUB_REPLY, Arrays.asList(ID_USER2_26_PUB));
    }

    /**
     * test: get fourth slice of 10 items - there shouldn't be one
     */
    @Test
    public void testFourthBunchOfTenItems() {

        // retrieve third 10 annotations
        final AnnotationSearchOptions options = new AnnotationSearchOptions(
                DOC_URL, WORLDGROUP,  // URI, group
                true,                 // provide separate replies
                10, 30,               // limit, offset (10/30 -> fourth bunch of ten items)
                ASC, CREATED);        // order, sort column

        // no items found!
        final AnnotationSearchResult annots = annotService.searchAnnotations(options, userInfo);
        Assert.assertEquals(0, annots.size());
        Assert.assertEquals(29, annots.getTotalItems());  // still returns total number, even if none are returned at all

        final List<Annotation> replies = annotService.searchRepliesForAnnotations(annots, options, userInfo);
        Assert.assertEquals(0, replies.size());
    }

    private void assertItemAndReplies(final JsonAnnotation annot, final String itemId, final List<String> replyParentIds) {

        Assert.assertNotNull(annot);
        Assert.assertEquals(itemId, annot.getId());
        if (replyParentIds == null) {
            Assert.assertNull(annot.getReferences());
        } else {
            Assert.assertArrayEquals(replyParentIds.toArray(), annot.getReferences().toArray());
        }
        Assert.assertNull(annot.getLinkedAnnotationId());
    }
}
