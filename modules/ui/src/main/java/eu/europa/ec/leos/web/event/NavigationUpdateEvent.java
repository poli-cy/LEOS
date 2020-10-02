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
package eu.europa.ec.leos.web.event;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

public class NavigationUpdateEvent {

    private String viewId;
    private String viewKey;

    public NavigationUpdateEvent(@Nonnull String viewId, @Nonnull String viewKey) {
        Validate.notNull(viewId, "The navigation view id must not be null!");
        Validate.notNull(viewKey, "The navigation view key must not be null!");
        this.viewId = viewId;
        this.viewKey = viewKey;
    }

    public @Nonnull String getViewId() {
        return viewId;
    }

    public @Nonnull String getViewKey() {
        return viewKey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NavigationUpdateEvent{");
        sb.append("viewId='").append(viewId).append('\'');
        sb.append(", viewKey='").append(viewKey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
