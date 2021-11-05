package gov.sec.idap.maxds.elasticsearch.service;

import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.repository.EntityRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EntityService {

    private final EntityRepository repository;
    private final RestHighLevelClient client;
    private ObjectMapper objectMapper;

    @Autowired
    public EntityService(EntityRepository repository, RestHighLevelClient client, ObjectMapper objectMapper) {
        this.repository = repository;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public void save(final Entity entity) {
        repository.save(entity);
    }

    public Entity findById(final String id) {
        return repository.findById(id).orElse(null);
    }
    
	public void saveAllEntity(List<Entity> entities) {
		repository.saveAll(entities);
	}

	public Iterable<Entity> findAllEntities() {
		return repository.findAll();
	}

	public List<Entity> findByCik(String cik) {
		StringBuffer sb = new StringBuffer("0000000000"); //cik string size is 10
	    sb.append(cik.trim());
	    String formatted = sb.substring(sb.length()-10, sb.length());
		return repository.findByCik(formatted);
	}
	
	public List<Entity> findByName(String name) {
		return searchString("companyName", name);
	}
	
	public List<Entity> findBySector(String name) {
		return searchString("sector", name);
	}
	
	public List<Entity> findByDivision(String name) {
		return searchString("division", name);
	}
	
	public List<Entity> findByIndustry(String name) {
		return searchString("industry", name);
	}
	
	public List<Entity> findByFilerCategory(String name) {
		return searchString("filerCategory", name);
	}
	
	private List<Entity> searchString(String field, String name) {
		String companyName = name.toLowerCase() + "*";
		
		SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //QueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("company_name", companyName);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery(field, companyName));
		
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

    private List<Entity> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<Entity> entities = new ArrayList<>();

        for (SearchHit hit : searchHit){
        	entities
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), Entity.class));
        }

        return entities;
    }
	
}
