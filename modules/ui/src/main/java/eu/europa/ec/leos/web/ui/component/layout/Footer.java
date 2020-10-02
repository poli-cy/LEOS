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
package eu.europa.ec.leos.web.ui.component.layout;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import eu.europa.ec.leos.web.support.LeosBuildInfo;
import eu.europa.ec.leos.i18n.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class Footer extends CustomComponent {

    private static final long serialVersionUID = -424441011003896920L;

    private static final Logger LOG = LoggerFactory.getLogger(Footer.class);

    private MessageHelper msgHelper;

    public Footer(final MessageHelper msgHelper) {
        this.msgHelper = msgHelper;

        LOG.trace("Initializing footer...");
        initLayout();

    }

    // initialize footer layout
    private void initLayout() {
        // create footer layout
        final VerticalLayout footerLayout = new VerticalLayout();
        footerLayout.setMargin(false);
        footerLayout.setSpacing(false);
        footerLayout.addStyleName("leos-footer-layout");
        footerLayout.setHeight("20px");

        // set footer layout as composition root
        setCompositionRoot(footerLayout);
        addStyleName("leos-footer");

        // info
        final Component info = buildFooterInfo();
        footerLayout.addComponent(info);
        footerLayout.setComponentAlignment(info, Alignment.MIDDLE_CENTER);
    }

    private @Nonnull
    Component buildFooterInfo() {
        final String infoMsg = msgHelper.getMessage(
                "leos.ui.footer.info",
                LeosBuildInfo.BUILD_VERSION,
                LeosBuildInfo.SOURCE_REVISION,
                LeosBuildInfo.BUILD_DATE);

        final Label infoLabel = new Label(infoMsg);
        infoLabel.setSizeUndefined();
        return infoLabel;
    }
}
