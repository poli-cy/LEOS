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

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TOKENS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ACCESS_TOKEN"}),
        @UniqueConstraint(columnNames = {"REFRESH_TOKEN"})
})
public class Token {

    /**
     * Class representing a set of access and refresh tokens given to a user 
     */

    // -------------------------------------
    // column definitions
    // -------------------------------------

    @Id
    @Column(name = "ID", nullable = false)
    @GenericGenerator(name = "tokensSequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "TOKENS_SEQ"),
            @Parameter(name = "increment_size", value = "1")
    })
    @GeneratedValue(generator = "tokensSequenceGenerator")
    @SuppressWarnings("PMD.ShortVariable")
    private long id;

    // user ID column, filled by hibernate
    @Column(name = "USER_ID", insertable = false, updatable = false, nullable = false)
    private long userId;

    // associate user, mapped by hibernate using USERS.USER_ID column
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    // authority for which the user's token is issued
    @Column(name = "AUTHORITY", nullable = false)
    private String authority;
    
    // access token granted to the user
    @Column(name = "ACCESS_TOKEN", nullable = false, unique = true)
    private String accessToken;

    // date/time of expiration of the access token
    @Column(name = "ACCESS_TOKEN_EXPIRES", nullable = false)
    private LocalDateTime accessTokenExpires;

    // the access token's TTL in seconds - property contained here for easier access to this value
    @Transient
    @SuppressWarnings("PMD.LongVariable")
    private int accessTokenLifetimeSeconds;

    // refresh token granted to the user
    @Column(name = "REFRESH_TOKEN", nullable = false, unique = true)
    private String refreshToken;

    // date/time of expiration of the refresh token
    @Column(name = "REFRESH_TOKEN_EXPIRES", nullable = false)
    private LocalDateTime refreshTokenExpires;

    // the refresh token's TTL in seconds - property contained here for easier access to this value
    @Transient
    @SuppressWarnings("PMD.LongVariable")
    private int refreshTokenLifetimeSeconds;

    // -----------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------

    public Token() {
        // parameterless constructor required by JPA
    }

    public Token(final User user, final String authority, 
            final String accessToken, final LocalDateTime accessTokenExpiration, 
            final String refreshToken, final LocalDateTime refreshTokenExpiration) {

        this.user = user;
        this.userId = user.getId();
        this.authority = authority;
        this.accessToken = accessToken;
        this.accessTokenExpires = accessTokenExpiration;
        this.refreshToken = refreshToken;
        this.refreshTokenExpires = refreshTokenExpiration;
    }

    // -----------------------------------------------------------
    // Useful getters & setters (exceeding POJO)
    // -----------------------------------------------------------

    public void setAccessToken(final String accessToken, final int lifetimeSeconds) {
        setAccessToken(accessToken);
        setAccessTokenLifetimeSeconds(lifetimeSeconds);

        setAccessTokenExpires(LocalDateTime.now().plusSeconds(lifetimeSeconds));
    }

    public void setRefreshToken(final String refreshToken, final int lifetimeSeconds) {
        setRefreshToken(refreshToken);
        setRefreshTokenLifetimeSeconds(lifetimeSeconds);

        setRefreshTokenExpires(LocalDateTime.now().plusSeconds(lifetimeSeconds));
    }

    public boolean isAccessTokenExpired() {
        return this.accessTokenExpires.isBefore(LocalDateTime.now());
    }

    public boolean isRefreshTokenExpired() {
        return this.refreshTokenExpires.isBefore(LocalDateTime.now());
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
    public long getUserId() {
        return userId;
    }

    @Generated
    public void setUserId(final long userId) {
        this.userId = userId;
    }

    @Generated
    public User getUser() {
        return user;
    }

    @Generated
    public void setUser(final User user) {
        this.user = user;
    }

    @Generated
    public String getAuthority() {
        return authority;
    }
    
    @Generated
    public void setAuthority(final String auth) {
        this.authority = auth;
    }
    
    @Generated
    public String getAccessToken() {
        return accessToken;
    }

    @Generated
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    @Generated
    public LocalDateTime getAccessTokenExpires() {
        return accessTokenExpires;
    }

    @Generated
    public void setAccessTokenExpires(final LocalDateTime accessTokenExpires) {
        this.accessTokenExpires = accessTokenExpires;
    }

    @Generated
    public String getRefreshToken() {
        return refreshToken;
    }

    @Generated
    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Generated
    public LocalDateTime getRefreshTokenExpires() {
        return refreshTokenExpires;
    }

    @Generated
    public void setRefreshTokenExpires(final LocalDateTime refreshTokenExpires) {
        this.refreshTokenExpires = refreshTokenExpires;
    }

    @Generated
    public int getAccessTokenLifetimeSeconds() {
        return accessTokenLifetimeSeconds;
    }

    @Generated
    public void setAccessTokenLifetimeSeconds(final int ttl) {
        this.accessTokenLifetimeSeconds = ttl;
    }

    @Generated
    public int getRefreshTokenLifetimeSeconds() {
        return refreshTokenLifetimeSeconds;
    }

    @Generated
    public void setRefreshTokenLifetimeSeconds(final int ttl) {
        this.refreshTokenLifetimeSeconds = ttl;
    }

    // -------------------------------------
    // equals and hashCode
    // -------------------------------------

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, user, authority, accessToken, accessTokenExpires, refreshToken, refreshTokenExpires);
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
        final Token other = (Token) obj;
        return Objects.equals(this.id, other.id) &&
                Objects.equals(this.userId, other.userId) &&
                Objects.equals(this.authority, other.authority) &&
                Objects.equals(this.user, other.user) &&
                Objects.equals(this.accessToken, other.accessToken) &&
                Objects.equals(this.accessTokenExpires, other.accessTokenExpires) &&
                Objects.equals(this.refreshToken, other.refreshToken) &&
                Objects.equals(this.refreshTokenExpires, other.refreshTokenExpires);
    }

}
