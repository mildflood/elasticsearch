package gov.sec.idap.maxds.elasticsearch.repository;

import gov.sec.idap.maxds.elasticsearch.document.Entity;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends ElasticsearchRepository<Entity, String> {
	
	public List<Entity> findByCik(String cik);
	
	public List<Entity> findByCompanyName(String name);
	
	@Query("{\"bool\" : {\"must\" : [ {\"field\" : {\"companyName\" : \"?0\"}} ]}}")
	public List<Entity> findByName(String name);
}