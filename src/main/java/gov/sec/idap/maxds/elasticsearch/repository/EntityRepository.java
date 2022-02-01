package gov.sec.idap.maxds.elasticsearch.repository;

import gov.sec.idap.maxds.elasticsearch.document.Entity;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends ElasticsearchRepository<Entity, String> {
	
	//search cik with exact matching with input text
		@Query("{\"bool\":{\"must\":[{\"match\":{\"cik\":{\"query\":\"?0\"}}}]}}")
		public List<Entity> findByCik(String cik);
		
		//search company name that has a word starting with input text
		@Query("{\"bool\":{\"must\":[{\"wildcard\":{\"companyName\":\"?0*\"}}]}}")
		public List<Entity> findByCompanyName(String name);
		
		//search industry that contains a string as input text
		@Query("{\"bool\":{\"must\":[{\"wildcard\":{\"industry\":\"*?0*\"}}]}}")
		public List<Entity> findByIndustry(String industry);
}