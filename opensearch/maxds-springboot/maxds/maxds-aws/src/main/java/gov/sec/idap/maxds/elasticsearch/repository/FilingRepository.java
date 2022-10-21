package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.FilingDoc;

@Repository
public interface FilingRepository extends ElasticsearchRepository<FilingDoc, String> {

	@Query(" \"match_all\": { \"boost\" : 1.2 }")
	public Page<FilingDoc> findAll();
	
}