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
package eu.europa.ec.leos.services.validation.handlers;

import eu.europa.ec.leos.domain.vo.DocumentVO;
import eu.europa.ec.leos.domain.vo.ErrorVO;
import eu.europa.ec.leos.services.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChildrenValidator implements Validator {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void validate(DocumentVO documentVO, final List<ErrorVO> result) {
        ValidatorFactory validatorFactory = applicationContext.getBean(ValidatorFactory.class);
        if (documentVO.getChildDocuments() != null) {
            for (DocumentVO child : documentVO.getChildDocuments()) {
                validatorFactory
                        .getValidationChain(child)
                        .validate(child, result);
            }
        }
    }
}
