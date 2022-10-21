package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.TermMapInformationDoc;

@Repository
public interface TermMapInformationRepository extends ElasticsearchRepository<TermMapInformationDoc, String> {
    public List<TermMapInformationDoc> findByMapName(String id); 
    
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"mapName_s\" : \"?0\" }}  \r\n"
    		+ "      ], \r\n"
    		+ "      \"filter\":[\r\n"
    		+ "                { \"terms\":  { \"termId_s\": ?1 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  }")
    public List<TermMapInformationDoc> findByMapNameAndTermIdIn(String id, Collection<String> termIds, Pageable pageable); 

    public void deleteByTermIdAndMapName(String termId, String mapName);
    public void deleteByTermId(String termId);
    
    public TermMapInformationDoc findOneByTermIdAndMapName(String termId, String mapName);
    public TermMapInformationDoc findOneByTermId(String termId);
    
    public void deleteByMapName(String mapName);
    
	@Query(" \"match_all\": { \"boost\" : 1.2 }")
	public Page<TermMapInformationDoc> findAll();
}
