package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import gov.sec.idap.maxds.elasticsearch.document.Entity;

@Repository
public interface EntityRepository extends ElasticsearchRepository<Entity, String> {

//	@Override
//	public Iterable<Entity> findAll();
	
	@Query(" \"match_all\": { \"boost\" : 1.2 }")
	public Page<Entity> findAll();

	// search cik with exact matching with input text
	@Query("{\"bool\":{\"must\":[{\"match\":{\"cik\":{\"query\":\"?0\"}}}]}}")
	public List<Entity> findByCik(String cik);

	// search company name that has a word starting with input text
	@Query("{\"bool\":{\"must\":[{\"wildcard\":{\"companyName\":\"?0*\"}}]}}")
	public List<Entity> findByCompanyName(String name);

	// search industry that contains a string as input text
	@Query("{\"bool\":{\"must\":[{\"wildcard\":{\"industry\":\"*?0*\"}}]}}")
	public List<Entity> findByIndustry(String industry);
	
	@Query("{ \r\n"
			+ "      \"bool\": {\r\n"
			+ "          \"must\": [\r\n"
			+ "                {\"match\": {\"division\": \"?0\"}},\r\n"
			+ "                {\"match\": {\"sector.keyword\": \"?1\"}},\r\n"
			+ "				   {\"match\": {\"sic\": \"?2\"}},\r\n"
			+ "				   {\"match\": {\"filerCategory.keyword\": \"?3\"}}\r\n"
			+ "              ]\r\n"
			+ "    }\r\n"
			+ "  }")
	public List<Entity> findByDivisionSectorSicAndFilerCategory(String division, String sector, String sic, String filerCategory , Pageable pagebale);

}