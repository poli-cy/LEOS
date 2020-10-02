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
package eu.europa.ec.leos.services.content;

import eu.europa.ec.leos.services.util.TestUtils;
import eu.europa.ec.leos.vo.catalog.CatalogItem;
import eu.europa.ec.leos.vo.catalog.CatalogItem.ItemType;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TemplateServiceImplTest {

//    private TemplateServiceImpl templateServiceImpl = new TemplateServiceImpl();

    @Test
    @Ignore
    public void testGetTemplatesCatalog() throws Exception {

        InputStream fileContent = IOUtils.toInputStream(new String(TestUtils.getFileContent("/catalogTest.xml")));

        // DO THE ACTUAL TEST
        // FIXME uncomment code and implement test
//        List<CatalogItem> catalogItems = templateServiceImpl.getTemplatesCatalog(fileContent);
//
//        assertThat(catalogItems.size(), is(1));
//        assertThat(catalogItems.get(0).getDescription("EN"), is("Interinstitutional proposals - Law Initiative (COM/JOIN)"));
//        assertThat(catalogItems.get(0).getItems().size(), is(1));
//        assertThat(catalogItems.get(0).getItems().get(0).getId(), is("c1.1.1"));
//        assertThat(catalogItems.get(0).getItems().get(0).getItems().size(), is(2));
//        assertThat(catalogItems.get(0).getItems().get(0).getItems().get(0).getName("FR"),
//                is("SJ-023 - Proposition de rÃ¨glement du Parlement europÃ©en et du Conseil"));
//        assertThat(catalogItems.get(0).getItems().get(0).getItems().get(1).getTocItem(), is(ItemType.TEMPLATE));
    }

}
