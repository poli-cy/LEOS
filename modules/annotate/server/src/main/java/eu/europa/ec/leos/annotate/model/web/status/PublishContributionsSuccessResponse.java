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
package eu.europa.ec.leos.annotate.model.web.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.europa.ec.leos.annotate.Generated;

import java.util.Objects;

/**
 * Class representing the simple structure transmitted as response in case of successful publication of a contributor's annotations 
 */
@JsonIgnoreProperties(ignoreUnknown = true) // required to avoid deserialisation failures for constant field 'published'
public class PublishContributionsSuccessResponse {

    private static final boolean published = true;

    // -------------------------------------
    // Constructor
    // -------------------------------------

    @SuppressWarnings("PMD.UnnecessaryConstructor")
    public PublishContributionsSuccessResponse() {
        // default constructor required for deserialisation
    }

    // -------------------------------------
    // Getters & setters
    // -------------------------------------

    @Generated
    @SuppressWarnings("PMD.BooleanGetMethodName")
    public boolean getUpdated() {
        return published;
    }

    // -------------------------------------
    // equals and hashCode
    // -------------------------------------

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(published);
    }

    @Generated
    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return true; // since we only have a single static field
    }
}
