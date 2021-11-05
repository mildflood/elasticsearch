package gov.sec.idap.maxds.elasticsearch.repository;

import gov.sec.idap.maxds.elasticsearch.document.TermruleDoc;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermruleRepository extends ElasticsearchRepository<TermruleDoc, String> {
	
	//public List<Termrule> findByTerm_id(String termid);
	//public List<Termrule> findByTermName(String name);
}