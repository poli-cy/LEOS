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
package eu.europa.ec.leos.repository.document;

import eu.europa.ec.leos.domain.cmis.LeosCategory;
import eu.europa.ec.leos.domain.cmis.common.VersionType;
import eu.europa.ec.leos.domain.cmis.document.Annex;
import eu.europa.ec.leos.domain.cmis.metadata.AnnexMetadata;
import eu.europa.ec.leos.repository.LeosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Annex Repository implementation.
 *
 * @constructor Creates a specific Annex Repository, injected with a generic LEOS Repository.
 */
@Repository
public class AnnexRepositoryImpl implements AnnexRepository {

    private static final Logger logger = LoggerFactory.getLogger(AnnexRepositoryImpl.class);

    private final LeosRepository leosRepository;

    @Autowired
    public AnnexRepositoryImpl(LeosRepository leosRepository) {
        this.leosRepository = leosRepository;
    }

    @Override
    public Annex createAnnex(String templateId, String path, String name, AnnexMetadata metadata) {
        logger.debug("Creating Annex... [template=" + templateId + ", path=" + path + ", name=" + name + "]");
        return leosRepository.createDocument(templateId, path, name, metadata, Annex.class);
    }

    @Override
    public Annex createAnnexFromContent(String path, String name, AnnexMetadata metadata, byte[] content) {
        logger.debug("Creating Annex From Content... [tpath=" + path + ", name=" + name + "]");
        return leosRepository.createDocumentFromContent(path, name, metadata, Annex.class,
                LeosCategory.ANNEX.name(), content);
    }

    @Override
    public Annex updateAnnex(String id, AnnexMetadata metadata) {
        logger.debug("Updating Annex metadata... [id=" + id + "]");
        return leosRepository.updateDocument(id, metadata, Annex.class);
    }

    @Override
    public Annex updateAnnex(String id, byte[] content, VersionType versionType, String comment) {
        logger.debug("Updating Annex content... [id=" + id + "]");
        return leosRepository.updateDocument(id, content, versionType, comment, Annex.class);
    }

    @Override
    public Annex updateAnnex(String id, AnnexMetadata metadata, byte[] content, VersionType versionType, String comment) {
        logger.debug("Updating Annex metadata and content... [id=" + id + "]");
        return leosRepository.updateDocument(id, metadata, content, versionType, comment, Annex.class);
    }

    @Override
    public Annex updateMilestoneComments(String id, List<String> milestoneComments, byte[] content, VersionType versionType, String comment) {
        logger.debug("Updating Annex milestoneComments and content... [id=" + id + "]");
        return leosRepository.updateMilestoneComments(id, content, milestoneComments, versionType, comment, Annex.class);
    }

    @Override
    public Annex updateMilestoneComments(String id, List<String> milestoneComments) {
        logger.debug("Updating Annex milestoneComments... [id=" + id + "]");
        return leosRepository.updateMilestoneComments(id, milestoneComments, Annex.class);
    }

    @Override
    public Annex findAnnexById(String id, boolean latest) {
        logger.debug("Finding Annex by ID... [id=" + id + ", latest=" + latest + "]");
        return leosRepository.findDocumentById(id, Annex.class, latest);
    }

    @Override
    public void deleteAnnex(String id) {
        logger.debug("Deleting Annex... [id=" + id + "]");
        leosRepository.deleteDocumentById(id);
    }

    @Override
    public List<Annex> findAnnexVersions(String id, boolean fetchContent) {
        logger.debug("Finding Annex versions... [id=" + id + "]");
        return leosRepository.findDocumentVersionsById(id, Annex.class, fetchContent);
    }

    @Override
    public Annex findAnnexByRef(String ref) {
        logger.debug("Finding Annex by ref... [ref=" + ref + "]");
        return leosRepository.findDocumentByRef(ref, Annex.class);
    }
    
    @Override
    public List<Annex> findAllMinorsForIntermediate(String docRef, String currIntVersion, String prevIntVersion, int startIndex, int maxResults) {
        logger.debug("Finding Annex versions between intermediates...");
        return leosRepository.findAllMinorsForIntermediate(Annex.class, docRef, currIntVersion, prevIntVersion, startIndex, maxResults);
    }
    
    @Override
    public int findAllMinorsCountForIntermediate(String docRef, String currIntVersion, String prevIntVersion) {
        logger.debug("Finding Annex minor versions count between intermediates...");
        return leosRepository.findAllMinorsCountForIntermediate(Annex.class, docRef, currIntVersion, prevIntVersion);
    }
    
    @Override
    public Integer findAllMajorsCount(String docRef) {
        return leosRepository.findAllMajorsCount(Annex.class, docRef);
    }
    
    @Override
    public List<Annex> findAllMajors(String docRef, int startIndex, int maxResult) {
        return leosRepository.findAllMajors(Annex.class, docRef, startIndex, maxResult);
    }
    
    @Override
    public List<Annex> findRecentMinorVersions(String documentId, String documentRef, int startIndex, int maxResults) {
        final Annex annex = leosRepository.findLatestMajorVersionById(Annex.class, documentId);
        return leosRepository.findRecentMinorVersions(Annex.class, documentRef, annex.getCmisVersionLabel(), startIndex, maxResults);
    }
    
    @Override
    public Integer findRecentMinorVersionsCount(String documentId, String documentRef) {
        final Annex annex = leosRepository.findLatestMajorVersionById(Annex.class, documentId);
        return leosRepository.findRecentMinorVersionsCount(Annex.class, documentRef, annex.getCmisVersionLabel());
    }
}
