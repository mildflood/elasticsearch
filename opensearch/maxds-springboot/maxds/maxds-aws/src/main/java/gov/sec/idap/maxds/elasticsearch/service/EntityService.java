package gov.sec.idap.maxds.elasticsearch.service;

import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.repository.EntityRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("entityService")
public class EntityService {

	@Autowired
    private EntityRepository repository;
	@Autowired
    private RestHighLevelClient client;
	@Autowired
    private ObjectMapper objectMapper;
    
    //@Autowired
    //private SecApiService idapAPIService;

//    @Autowired
//    public EntityService(EntityRepository repository, RestHighLevelClient client, ObjectMapper objectMapper) {
//        this.repository = repository;
//        this.client = client;
//        this.objectMapper = objectMapper;
//    }

    public void save(final Entity entity) {
        repository.save(entity);
    }

    public Optional<Entity> findById(final String id) {
       // return repository.findfindById(id).orElse(null);
        return repository.findById(id);
    }
    public Entity getEntityById(String id) {
        //String query = String.format("entityId:%s", id);
        List<Entity> result = findByCik(id); //idapAPIService.getEntitiesByQuery(query);
        if (result != null && result.size() == 1) {
            return result.get(0);
        }
        return null;
    }
    
    
	public void saveAllEntity(List<Entity> entities) {
		repository.saveAll(entities);
	}

	public Iterable<Entity> findAllEntities() {
		return repository.findAll().toList();
	}

	public List<Entity> findByCik(String cik) {
		StringBuffer sb = new StringBuffer("0000000000"); //cik string size is 10
	    sb.append(cik.trim());
	    String formatted = sb.substring(sb.length()-10, sb.length());
		return repository.findByCik(formatted);
	}
	
	public List<Entity> findByName(String name) {
		//return searchString("companyName", name);
		return repository.findByCompanyName(name);
	}
	
	public List<Entity> findBySector(String name) {
		return searchString("sector", name);
	}
	
	public List<Entity> findByDivision(String name) {
		return searchString("division", name);
	}
	
	public List<Entity> findByIndustry(String name) {
		//return searchString("industry", name);
		return repository.findByIndustry(name);
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
    
    public HashMap<String, Entity> findEntitiesByDivisionSectorAndSicCode(
            final String division,
            final String sector,
            final String sic,
            final String filerCategory) {

       String query = buildQuery(division, sector, sic, filerCategory);
//        List<Entity> result 
//                = idapAPIService.getEntitiesByQuery(query);
        
        List<Entity> results = repository.findByDivisionSectorSicAndFilerCategory(division, sector, sic, filerCategory, PageRequest.of(0, 2000));
        
//        HashMap<String, Entity> ret = new HashMap<>();
        HashMap<String, Entity> map = new HashMap<>();
//        for (Entity doc : result) {
//            ret.put(doc.getEntityId(), doc);
//        }
        for (Entity doc : results) {
            map.put(doc.getEntityId(), doc);
        }
        return map;
    }
    
    public HashMap<String, String> findByDivisionSectorAndSicCode(
            final String division,
            final String sector,
            final String sic,
            final String filerCategory) {

        String fields = "entityId,companyName";

        String query = buildQuery(division, sector, sic, filerCategory);
        //List<Entity> result 
        //        = idapAPIService.getEntityFieldsByQuery(query, fields);
        
        List<Entity> results = repository.findByDivisionSectorSicAndFilerCategory(division, sector, sic, filerCategory, PageRequest.of(0, 2000));
        
//        HashMap<String, String> ret = new HashMap<>();
        HashMap<String, String> map = new HashMap<>();
//        for (Entity doc : result) {
//            ret.put(doc.getEntityId(), doc.getCompanyName());
//        }
        for (Entity doc : results) {
            map.put(doc.getEntityId(), doc.getCompanyName());
        }
        return map;
    }
    public long count() {
        return getAllByFields("entityId").size();
    }

    private List<Entity> getAllByFields(String field) {
        //List<Entity> result =  idapAPIService.getAllEntities(field);
    	List<Entity> results = new ArrayList<Entity>();
    	//return the same entity list as findAllByFields
    	results = getEntityList(repository.findAll().toList()); //findAllByFields(field);
    	return results;
    }
    
	private List<Entity> getEntityList(Iterable<Entity> entities) {
		List<Entity> trFullList = new ArrayList<Entity>();
		for (Entity tr : entities) {
			trFullList.add(tr);
		}
		return trFullList;
	}
	
    private String buildQuery(final String division, final String sector, final String sic, final String filerCategory) {
        String query = "";
        if (division != null && !division.isEmpty() && !division.equals("NULL")) {

            query += String.format("division:\"%s\" AND ", division);

        }
        if (sector != null && !sector.isEmpty() && !sector.equals("NULL")) {

            query += String.format("sector:\"%s\" AND ", sector);

        }
        if (sic != null && !sic.isEmpty() && !sic.equals("NULL")) {

            query += String.format("sic:\"%s\" AND ", sic);

        }
        if (filerCategory != null && !filerCategory.isEmpty() && !filerCategory.equals("NULL")) {

            query += String.format("filerCategory:\"%s\" AND ", filerCategory);

        }
        if( query.length() > 0)
        {
            query = query.substring(0,query.length()-5 );
        }
        return query;
    }
    
    public List<Entity> findLimitedSlow() {

        String fields = "entityId,companyName,cik,division,sector,sic,industry,filerCategory";
        return getAllByFields(fields);

    }
	
}
