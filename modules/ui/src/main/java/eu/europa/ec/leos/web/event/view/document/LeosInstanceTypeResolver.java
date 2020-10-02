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

import eu.europa.ec.leos.domain.common.InstanceType;
import eu.europa.ec.leos.model.user.User;
import eu.europa.ec.leos.instance.Instance;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
@Instance(InstanceType.OS)
public class LeosInstanceTypeResolver implements InstanceTypeResolver {

    @Override
    @Nonnull public EditElementResponseEvent createEvent(String elementId, String elementTagName, String elementFragment, String docType, User user,
            String[] authorities, String alternatives) {
        return new EditElementResponseEvent(elementId, elementTagName, elementFragment, docType, user, authorities, InstanceType.OS.name(), alternatives);
    }

    @Override
    public String getInstanceType() {
        return InstanceType.OS.name();
    }
}
