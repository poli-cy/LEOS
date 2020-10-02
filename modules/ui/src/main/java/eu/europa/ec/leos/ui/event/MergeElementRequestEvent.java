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
package eu.europa.ec.leos.ui.event;

public class MergeElementRequestEvent {

    private String elementId;
    private String elementTagName;
    private String elementContent;

    public MergeElementRequestEvent(String elementId, String elementTagName, String elementContent) {
        this.elementId = elementId;
        this.elementTagName = elementTagName;
        this.elementContent = elementContent;
    }

    public String getElementId() {
        return elementId;
    }

    public String getElementTagName() {
        return elementTagName;
    }

    public String getElementContent() {
        return elementContent;
    }

}
