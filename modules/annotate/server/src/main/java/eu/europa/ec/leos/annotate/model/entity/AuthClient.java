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
package eu.europa.ec.leos.annotate.model.entity;

import eu.europa.ec.leos.annotate.Generated;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "AUTHCLIENTS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"SECRET"}),
        @UniqueConstraint(columnNames = {"CLIENT_ID"})
})
public class AuthClient {

    /**
     * Class representing an annotate client trying to authenticate users 
     */

    // -------------------------------------
    // column definitions
    // -------------------------------------

    @Id
    @Column(name = "ID", nullable = false)
    @GenericGenerator(name = "authclientsSequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "AUTHCLIENTS_SEQ"),
            // @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "1")
    })
    @GeneratedValue(generator = "authclientsSequenceGenerator")
    @SuppressWarnings("PMD.ShortVariable")
    private long id;

    // human-readable description
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    // secret key of the client
    @Column(name = "SECRET", nullable = false, unique = true)
    private String secret;

    // ID of the client; used in "issuer" field of JWT
    @Column(name = "CLIENT_ID", nullable = false, unique = true)
    private String clientId;

    // list of authorities that the client may authenticate; separated by semi-colon
    @Column(name = "AUTHORITIES", nullable = true)
    private String authorities;

    // -----------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------
    public AuthClient() {
        // default constructor required by JPA
    }

    public AuthClient(final String description, final String secret, final String clientId, final String authorities) {

        this.description = description;
        this.secret = secret;
        this.clientId = clientId;
        this.authorities = authorities;
    }

    // -----------------------------------------------------------
    // Getters & setters
    // -----------------------------------------------------------
    
    @Generated
    public long getId() {
        return id;
    }

    @Generated
    public void setId(final long newId) {
        this.id = newId;
    }

    @Generated
    public String getDescription() {
        return description;
    }

    @Generated
    public void setDescription(final String description) {
        this.description = description;
    }

    @Generated
    public String getSecret() {
        return secret;
    }

    @Generated
    public void setSecret(final String secret) {
        this.secret = secret;
    }

    @Generated
    public String getClientId() {
        return clientId;
    }

    @Generated
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    @Generated
    public String getAuthorities() {
        return authorities;
    }

    @Generated
    public void setAuthorities(final String authorities) {
        this.authorities = authorities;
    }

    // return a list of the individual authorities for which the client may authenticate
    @Transient
    public List<String> getAuthoritiesList() {

        if (this.authorities == null) {
            return null;
        }
        return Arrays.asList(this.authorities.split(";"));
    }

    // -------------------------------------
    // equals and hashCode
    // -------------------------------------

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(id, description, secret, clientId, authorities);
    }

    @Generated
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AuthClient other = (AuthClient) obj;
        return Objects.equals(this.id, other.id) &&
                Objects.equals(this.description, other.description) &&
                Objects.equals(this.secret, other.secret) &&
                Objects.equals(this.clientId, other.clientId) &&
                Objects.equals(this.authorities, other.authorities);
    }
}
