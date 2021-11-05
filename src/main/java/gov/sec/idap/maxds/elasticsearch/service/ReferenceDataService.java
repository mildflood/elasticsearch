/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.elasticsearch.service;

import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.repository.LookupDocRepository;

import java.io.IOException;
//import gov.sec.idap.maxds.solr.document.LookupDoc;
//import gov.sec.idap.maxds.solr.repository.LookupDocRepository;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("referenceDataService")
public class ReferenceDataService {
	
	private final LookupDocRepository repository;
    private final RestHighLevelClient client;
    private ObjectMapper objectMapper;

    @Autowired
    public ReferenceDataService(LookupDocRepository repository, RestHighLevelClient client, ObjectMapper objectMapper) {
        this.repository = repository;
        this.client = client;
        this.objectMapper = objectMapper;
    }
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    public List<LookupDoc> findAll() {
    	return repository.findAll();
    }
    
    public List<LookupDoc> getAllMappingGroups() {
    	
    	List<LookupDoc> serverList = null;
    	//serverList = repository.findNameByType(LookupDoc.LookupType.termMapGroup.toString());

        try {
            // Build Query
            MatchQueryBuilder queryBuilder = new MatchQueryBuilder("type_s", LookupDoc.LookupType.termMapGroup.toString());
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery().must(queryBuilder);
            // Create the searchObjectIdsViaExpression source
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(filterQuery);
            // Generate the actual request to send to ES.
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            
            SearchHit[] hits = response.getHits().getHits();
            serverList = new ArrayList<>(hits.length);
            serverList = getSearchResult(response);
          }catch(Exception e) {
            log.error("Failed");
          }
        return serverList;
    }

    public void addGroup(String groupName) {

        LookupDoc doc = new LookupDoc();
        doc.setId(groupName);
        doc.setName(groupName);
        doc.setType(LookupDoc.LookupType.termMapGroup);

        repository.save(doc);
    }
    
     public void add(List<LookupDoc> lookupDocs) {

        List<LookupDoc> lookupData = new ArrayList<>();
        for (LookupDoc lc : lookupDocs) {
           
            if( this.findOne(lc.getName(), lc.getType().toString()) !=  null){
                //LOG.debug("skipping  CSV data to SOLR...");
                continue;
            }
          
          //LOG.debug("Adding  CSV data to SOLR...");
            lookupData.add(lc);
        }
        if( lookupData.size() > 0 ){
            
        	log.debug("Begin saving to elastic ................." + lookupData.size());
           repository.saveAll(lookupData); 
        }

    }

    public List<LookupDoc> findAllRuleCategories() {

        List<LookupDoc> serverList = repository.findNameByType(LookupDoc.LookupType.termRuleCategory.toString());
       
        return serverList;
    }

    public LookupDoc findOneTaxonomyElement(String id) {
    	//PageRequest request = new PageRequest(0, 1, Sort.by("id"));
    	Pageable paging = PageRequest.of(0, 1, Sort.by("id"));
        List<LookupDoc> serverList = repository.findTaxonomyElementByName(id, paging);

        if (serverList.size() == 1) {

            LookupDoc doc = serverList.get(0);
            return doc;
        }

        return null;
    }
    
     public LookupDoc findOne(String id, String type) {

        //PageRequest request = new PageRequest(0, 1);
    	 Pageable paging = PageRequest.of(0, 1, Sort.by("id"));
        List<LookupDoc> serverList = repository.findByNameAndType(id,type, paging);

        if (serverList.size() == 1) {
            return serverList.get(0);
        }

        return null;
    }


    public List<LookupDoc> findByTaxonomyElementIdLike(String query, Boolean isTextBlock) {

        //PageRequest request = new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "name_s"));
        Pageable paging = PageRequest.of(0, 20, Sort.by("name_s"));
        if (query.contains("us-gaap")) {
            query = query.replace("us-gaap", "gaap");
        }
        
        if (query.contains("ifrs-full")) {
            query = query.replace("ifrs-full", "full");
        }
         
         
        List<LookupDoc> serverList = repository.findByIsTextBlockAndNameContaining(isTextBlock, query, paging).getContent();

        return serverList;

    }
    
	private List<LookupDoc> searchString(String field, String str) {
		//String companyName = name.toLowerCase() + "*";
		String searchStr = str;
		
		SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //QueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("company_name", companyName);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery(field, searchStr));
		
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        return getSearchResult(response);
    }

    private List<LookupDoc> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<LookupDoc> docs = new ArrayList<>();

        for (SearchHit hit : searchHit){
        	docs
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), LookupDoc.class));
        }

        return docs;
    }

}
