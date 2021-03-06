/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;


import gov.sec.idap.maxds.solr.document.LookupDoc;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;


public interface LookupDocRepository extends SolrCrudRepository<LookupDoc, String> {
    
    
     @Query(value="type_s:?0")
    List<LookupDoc> findNameByType(String type);
    
    
    @Query(value="name_s:?0 AND type_s:taxonomyElement")
    List<LookupDoc> findTaxonomyElementByName(String name, Pageable pageable);
    
     @Query(value="name_s:\"?0\" AND type_s:?1")
    List<LookupDoc> findByNameAndType(String name, String type, Pageable pageable);
   
    
    @Query(value="type_s:taxonomyElement AND isTextBlock_b:?0 AND name_s:*?1*")
    Page<LookupDoc> findByIsTextBlockAndNameContaining(Boolean isTextBlock, 
            String name, Pageable pageable);
    
     Page<LookupDoc> findByNameLike(String name, Pageable pageable);
     
     void deleteByNameAndType(String name, String type);

}
