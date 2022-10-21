package gov.sec.idap.maxds.elasticsearch.repository;

import gov.sec.idap.maxds.elasticsearch.document.TermRuleDoc;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRuleRepository extends ElasticsearchRepository<TermRuleDoc, String> {
	
    //@Query("*:*")
    @Override
    public Iterable<TermRuleDoc> findAll();
    
    //@Query(value="termId:\"?0\"")
    //@Query("{\"bool\":{\"must\":[{\"wildcard\":{\"termId\":\"?0*\"}}]}}")
    @Query("{\"bool\":{\"must\":[{\"match\":{\"termId\":\"?0\"}}]}}")
    public List<TermRuleDoc> findByTermId(String searchTerm);
    
    //@Query(value="name:\"?0\"")
    @Query("{\"bool\":{\"must\":[{\"wildcard\":{\"name\":\"?0*\"}}]}}")
    public List<TermRuleDoc> findByTermName(String searchTerm);
    
    //@Query(value="*:*",  fields= "termId, name, description, financialStatement, priorityGroup,hasValidations")
    @Query("{\"fields\": [ \"termId\", \"name\", \"description\", \"financialStatement\", \"priorityGroup,hasValidations\"],\"_source\": false}")
    public List<TermRuleDoc> findLimited();
   
    //@Query(value="*:*", name="termId, name, description")
    @Query("{\"fields\": [ \"termId\", \"name\", \"description\"],\"_source\": false}")
    public List<TermRuleDoc> findNameInfos();
    
    public void deleteByTermId(String termId);
    
    //@Query(value="includeInAccuracyTests_b:true")
    //@Query("{ \"bool\": {\"must\":[{\"match\" : { \"includeInAccuracyTests_b\" : true }}]}}, \"from\":0,\"size\":100")
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"includeInAccuracyTests_b\" : true }}]}}")
    @Query("{\r\n"
    		+ "        \"term\" : { \"includeInAccuracyTests\" : true }\r\n"
    		+ " }")
    public List<TermRuleDoc> findByIncludeInAccuracyTests(Pageable pagebale);

   // @Query(value="termId:?0")
    @Query("{\r\n"
    		+ "        \"terms\" : { \"termId.keyword\" : ?0 }\r\n"
    		+ " }")
    public List<TermRuleDoc> findByTermIds(Collection<String> termId);
}