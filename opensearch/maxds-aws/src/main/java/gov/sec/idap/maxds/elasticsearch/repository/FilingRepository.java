package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.FilingDoc;

@Repository
public interface FilingRepository extends ElasticsearchRepository<FilingDoc, String> {

	@Override
	public Iterable<FilingDoc> findAll();
	
}