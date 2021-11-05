/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.elasticsearch.repository;

import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface LookupDocRepository extends ElasticsearchRepository<LookupDoc, String> {  
	
	List<LookupDoc> findAll();
	
	@Query("{\"bool\":{\"must\":[{\"match\":{\"type_s\":{\"query\":\"?0\"}}}]}}")
    List<LookupDoc> findNameByType(String type);
    
    @Query(value="name_s:?0 AND type_s:taxonomyElement")
    List<LookupDoc> findTaxonomyElementByName(String name, Pageable pageable);
    
    @Query("{\"bool\":{\"must\":[{\"wildcard\":{\"type_s\":{\"wildcard\":\"termMapGroup\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}")
     //@Query("{\"bool\" : {\"must\" : [ {\"field\" : {\"name_s\" : \"?0\"}}, {\"field\" : {\"type_s\" : \"?1\"}} ]}}")
    List<LookupDoc> findByNameAndType(String name, String type, Pageable pageable);
    
    @Query(value="type_s:taxonomyElement AND isTextBlock_b:?0 AND name_s:*?1*")
    Page<LookupDoc> findByIsTextBlockAndNameContaining(Boolean isTextBlock, 
            String name, Pageable pageable);    
    
    Page<LookupDoc> findByNameLike(String name, Pageable pageable);
     
    void deleteByNameAndType(String name, String type);
}
