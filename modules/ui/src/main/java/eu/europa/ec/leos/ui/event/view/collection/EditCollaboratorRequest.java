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
package eu.europa.ec.leos.ui.event.view.collection;

import eu.europa.ec.leos.web.model.CollaboratorVO;

public class EditCollaboratorRequest {
    private CollaboratorVO collaborator;
    private String proposalURL;

    public EditCollaboratorRequest(CollaboratorVO collaborator, String proposalURL) {
        this.collaborator = collaborator;
        this.proposalURL = proposalURL;
    }

    public CollaboratorVO getCollaborator() {
        return collaborator;
    }

    public String getProposalURL() {
        return proposalURL;
    }
}
