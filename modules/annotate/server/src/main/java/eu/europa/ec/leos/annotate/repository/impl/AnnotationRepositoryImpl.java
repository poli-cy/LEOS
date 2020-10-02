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
package eu.europa.ec.leos.annotate.repository.impl;

import eu.europa.ec.leos.annotate.repository.AnnotationRepositoryCustom;

// note: class name is important here, it must be the Impl to the 
// original repository, otherwise Spring won't be able to make the link 
public class AnnotationRepositoryImpl implements AnnotationRepositoryCustom {

    // this is an override of the CrudRepository interface function, which should not be used due to problems;
    // see comment on the customDeleteAll function in AnnotationRepository
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void deleteAll() {
        throw new RuntimeException("This method must not be called; call the customDeleteAll method instead.");
    }
}
