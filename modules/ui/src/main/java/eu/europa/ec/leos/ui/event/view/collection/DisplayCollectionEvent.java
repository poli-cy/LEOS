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


import eu.europa.ec.leos.domain.cmis.LeosCategory;

public class DisplayCollectionEvent {
    private String documentId;
    private LeosCategory documentType;

    public DisplayCollectionEvent(String documentId, LeosCategory documentType) {
        this.documentId = documentId;
        this.documentType = documentType;
    }

    public String getDocumentId(){
        return documentId;
    }

    public LeosCategory getDocumentType(){
        return documentType;
    }
}
