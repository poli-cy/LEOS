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
package eu.europa.ec.leos.ui.view.workspace;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.UI;
import eu.europa.ec.leos.domain.common.InstanceType;
import eu.europa.ec.leos.i18n.LanguageHelper;
import eu.europa.ec.leos.i18n.MessageHelper;
import eu.europa.ec.leos.instance.Instance;
import eu.europa.ec.leos.security.LeosPermissionAuthorityMapHelper;
import eu.europa.ec.leos.security.SecurityContext;
import eu.europa.ec.leos.ui.wizard.document.CreateMandateWizard;
import eu.europa.ec.leos.vo.catalog.CatalogItem;
import eu.europa.ec.leos.web.event.view.repository.MandateCreateWizardRequestEvent;
import eu.europa.ec.leos.web.support.user.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@ViewScope
@SpringComponent
@Instance(InstanceType.COUNCIL)
public class WorkspaceScreenMandateImpl extends WorkspaceScreenImpl {

    private static final long serialVersionUID = 1L;

    @Autowired
    WorkspaceScreenMandateImpl(SecurityContext securityContext, EventBus eventBus, MessageHelper messageHelper, LanguageHelper langHelper, UserHelper userHelper, LeosPermissionAuthorityMapHelper authorityMapHelper) {
        super(securityContext, eventBus, messageHelper, langHelper, userHelper, authorityMapHelper);
        initSpecificStaticData();
        initSpecificListeners();
    }

    private void initSpecificStaticData() {
        createDocumentButton.setCaption(messageHelper.getMessage("repository.create.mandate"));
        createDocumentButton.setDescription(messageHelper.getMessage("repository.create.mandate.tooltip"));

        resetBasedOnPermissions();
    }

    private void resetBasedOnPermissions() {
        uploadDocumentButton.setVisible(false);
    }

    private void initSpecificListeners() {
        createDocumentButton.addClickListener(clickEvent -> eventBus.post(new MandateCreateWizardRequestEvent()));
    }

    @Override
    public void showCreateMandateWizard() {
        CreateMandateWizard createMandateWizard = new CreateMandateWizard(messageHelper, langHelper, eventBus);
        UI.getCurrent().addWindow(createMandateWizard);
        createMandateWizard.focus();
    }

    @Override
    public void showCreateDocumentWizard(List<CatalogItem> catalogItems) {
    }
}
