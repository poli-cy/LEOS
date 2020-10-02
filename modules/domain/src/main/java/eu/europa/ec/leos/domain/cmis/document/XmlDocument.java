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
package eu.europa.ec.leos.domain.cmis.document;

import eu.europa.ec.leos.domain.cmis.Content;
import eu.europa.ec.leos.domain.cmis.LeosCategory;
import eu.europa.ec.leos.domain.cmis.common.Securable;
import eu.europa.ec.leos.domain.cmis.common.SecurityData;
import eu.europa.ec.leos.domain.cmis.common.VersionType;
import eu.europa.ec.leos.domain.cmis.metadata.LeosMetadata;
import io.atlassian.fugue.Option;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public abstract class XmlDocument extends LeosDocument implements Securable {
    private final String title;
    private final List<String> milestoneComments;
    private final SecurityData securityData;


    public final String getTitle() {
        return this.title;
    }


    public final List<String> getMilestoneComments() {
        return this.milestoneComments;
    }

    protected XmlDocument(LeosCategory category, String id, String name, String createdBy,
                          Instant creationInstant, String lastModifiedBy, Instant lastModificationInstant,
                          String versionSeriesId, String cmisVersionLabel, String versionLabel, String versionComment,
                          VersionType versionType, boolean isLatestVersion, String title,
                          Map<String, String> collaborators, List<String> milestoneComments, Option<Content> content) {
        super(category, id, name, createdBy, creationInstant, lastModifiedBy, lastModificationInstant, versionSeriesId,
                cmisVersionLabel, versionLabel, versionComment, versionType, isLatestVersion, content);
        this.securityData = new SecurityData(collaborators);
        this.title = title;
        this.milestoneComments = milestoneComments;
    }

    protected XmlDocument(LeosCategory category, String id, String name, String createdBy,
                          Instant creationInstant, String lastModifiedBy, Instant lastModificationInstant,
                          String versionSeriesId, String cmisVersionLabel, String versionLabel, String versionComment,
                          VersionType versionType, boolean isLatestVersion, Option<Content> content) {
        super(category, id, name, createdBy, creationInstant, lastModifiedBy, lastModificationInstant, versionSeriesId,
                cmisVersionLabel, versionLabel, versionComment, versionType, isLatestVersion, content);
        this.securityData = null;
        this.title = null;
        this.milestoneComments = null;
    }


    public Map<String, String> getCollaborators() {
        return this.securityData.getCollaborators();
    }

    public abstract Option<? extends LeosMetadata> getMetadata();

    public String getVersionedReference() {
        return this.getMetadata().get().getRef() + "_" + this.getVersionLabel();
    }
}
