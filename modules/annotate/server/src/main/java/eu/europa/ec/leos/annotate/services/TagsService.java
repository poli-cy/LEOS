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
package eu.europa.ec.leos.annotate.services;

import eu.europa.ec.leos.annotate.model.entity.Annotation;
import eu.europa.ec.leos.annotate.model.entity.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TagsService {

    /**
     * conversion of string list of tags to {@link Tag} objects associated to an annotation
     * 
     * @param tags
     *        list of tag names to be converted
     * @param annotation
     *        the {@link Annotation} to which the tags are to be assigned after conversion
     * @return converted list of tags
     */
    List<Tag> getTagList(List<String> tags, Annotation annotation);

    /**
     * removal of Tag objects from the database
     * 
     * @param tagsToRemove
     *        list of {@link Tag} items to remove
     */
    @Transactional
    void removeTags(List<Tag> tagsToRemove);

    /**
     * check if a tag identifying an annotation as a suggestion is present in a given list of tags
     *  
     * @param tags
     *        list of tags to be examined
     * @return flag indicating whether any of the tags can be identified as a suggestion tag
     */
    boolean hasSuggestionTag(List<Tag> itemTags);
    
    /**
     * check if a tag identifying an annotation as a highlight is present in a given list of tags
     *  
     * @param tags
     *        list of tags to be examined
     * @return flag indicating whether any of the tags can be identified as a highlight tag
     */
    boolean hasHighlightTag(List<Tag> tags);
}
