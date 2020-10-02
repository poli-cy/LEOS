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
package eu.europa.ec.leos.services.document;


import eu.europa.ec.leos.domain.cmis.document.XmlDocument;
import eu.europa.ec.leos.repository.store.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

// REFACTOR SecurityService API should probably be moved into WorkspaceService

@Service
class SecurityServiceImpl implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);

    private final WorkspaceRepository workspaceRepository;

    SecurityServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public <T extends XmlDocument> T addOrUpdateCollaborator(String id, String userLogin, String authority, Class<T> type) {
        T document = workspaceRepository.findDocumentById(id, type, true);
        Map<String, String> collaborators = document.getCollaborators();
        collaborators.put(userLogin, authority);
        return updateCollaborators(id, collaborators, type);
    }

    @Override
    public <T extends XmlDocument> T updateCollaborators(String id, Map<String, String> collaborators, Class<T> type) {
        return workspaceRepository.updateDocumentCollaborators(id, collaborators, type);
    }

    @Override
    public <T extends XmlDocument> T removeCollaborator(String id, String userLogin, Class<T> type) {
        T document = workspaceRepository.findDocumentById(id, type, true);
        Map<String, String> collaborators = document.getCollaborators();
        collaborators.remove(userLogin);
        return workspaceRepository.updateDocumentCollaborators(id, collaborators, type);
    }
}
