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
package eu.europa.ec.leos.ui.wizard.document;

import com.google.common.eventbus.EventBus;
import eu.europa.ec.leos.domain.cmis.LeosCategory;
import eu.europa.ec.leos.domain.vo.DocumentVO;
import eu.europa.ec.leos.i18n.LanguageHelper;
import eu.europa.ec.leos.i18n.MessageHelper;
import eu.europa.ec.leos.ui.event.CreateDocumentRequestEvent;
import eu.europa.ec.leos.ui.wizard.AbstractWizard;
import eu.europa.ec.leos.ui.wizard.WizardStep;
import eu.europa.ec.leos.vo.catalog.CatalogItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class CreateDocumentWizard extends AbstractWizard {

    private static final long serialVersionUID = 1L;

    private LanguageHelper languageHelper;
    private final DocumentVO document;

    public CreateDocumentWizard(List<CatalogItem> templates, MessageHelper messageHelper, LanguageHelper languageHelper, EventBus eventBus) {
        super(messageHelper, eventBus);
        this.languageHelper = languageHelper;
        document = new DocumentVO(LeosCategory.PROPOSAL);
        document.setUploaded(false);
        init(templates);
    }

    public void init(List<CatalogItem> templates) {
        // Currently there is only one use case: create proposal!
        // Ideally the category would be resolved from wizard data,
        // depending on some attribute of the selected template...
        registerWizardStep(new TemplateSelectionStep(document, templates, messageHelper));
        registerWizardStep(new MetadataInputStep(document, messageHelper, languageHelper));
        setWizardStep(0);
    }

    @Override
    protected String getWizardTitle() {
        return messageHelper.getMessage("wizard.document.create.title");
    }

    @Override
    protected boolean handleFinishAction(List<WizardStep> stepList) {
        eventBus.post(new CreateDocumentRequestEvent(document));
        return true;
    }
}
