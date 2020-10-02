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
package eu.europa.ec.leos.web.support.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Scope("session")
@Component("configurationHelper")
public class ConfigurationHelper {

    @Autowired
    @Qualifier("applicationProperties")
    private Properties applicationProperties;

    @Autowired
    @Qualifier("integrationProperties")
    private Properties integrationProperties;

    public String getProperty(String key) {
        return applicationProperties.getProperty(key);
    }

    public String getIntegrationProperty(String key) {
        return integrationProperties.getProperty(key);
    }
}
