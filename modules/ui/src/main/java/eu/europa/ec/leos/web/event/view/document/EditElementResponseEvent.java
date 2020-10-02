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
package eu.europa.ec.leos.web.event.view.document;

import eu.europa.ec.leos.model.user.User;

public class EditElementResponseEvent {

    private final String elementId;
    private final String elementTagName;
    private final String elementFragment;
    private final String docType;
    private final User user;
    private final String[] permissions;
    private final String instanceType;
    private final String alternatives;

    public EditElementResponseEvent(String elementId, String elementTagName, String elementFragment, String docType, User user, String[] permissions, String instanceType, String alternatives) {
        this.elementId = elementId;
        this.elementTagName = elementTagName;
        this.elementFragment = elementFragment;
        this.docType = docType;
        this.user = user;
        this.permissions = permissions;
        this.instanceType = instanceType;
        this.alternatives =  alternatives;
    }

    public String getElementId() {
        return elementId;
    }

    public String getElementTagName() {
        return elementTagName;
    }

    public String getElementFragment() {
        return elementFragment;
    }

    public String getDocType() {
        return docType;
    }

    public User getUser() {
        return user;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public String getAlternatives() {
        return alternatives;
    }
}