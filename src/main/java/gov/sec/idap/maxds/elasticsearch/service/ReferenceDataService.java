package gov.sec.idap.maxds.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.TermMapGroup;
import gov.sec.idap.maxds.domain.TermRuleCategory;
import gov.sec.idap.maxds.domain.TermRulePriorityGroup;
import gov.sec.idap.maxds.domain.USGAAPTaxonomyElement;
import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.helper.Indices;
import gov.sec.idap.maxds.elasticsearch.repository.LookupDocRepository;

@Service("referenceDataService")
public class ReferenceDataService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private LookupDocRepository repository;
	private RestHighLevelClient client;
	private ObjectMapper objectMapper;

	@Autowired
	public ReferenceDataService(LookupDocRepository repository, RestHighLevelClient client, ObjectMapper objectMapper) {
		this.repository = repository;
		this.client = client;
		this.objectMapper = objectMapper;
	}

	// use query builder and RestHighLevelClient
	public List<TermMapGroup> getAllMappingGroups() {
		List<LookupDoc> serverList = findNameByType(LookupDoc.LookupType.termMapGroup.toString());
		List<TermMapGroup> ret = new ArrayList<>();
		serverList.forEach((doc) -> {
			ret.add(new TermMapGroup(doc.getName()));
		});
		return ret;
	}
	
	public List<LookupDoc> findNameByType() {
		return repository.findNameByType(LookupDoc.LookupType.termMapGroup.toString());
	}

	// use custom @query from repo
//	public List<TermMapGroup> getAllMappingGroups() {
//    	List<LookupDoc> serverList = new ArrayList<LookupDoc>();
//    	serverList = repository.findNameByType(LookupDoc.LookupType.termMapGroup.toString());
//    	List<TermMapGroup> ret = new ArrayList<>();
//        serverList.forEach((doc) -> {
//            ret.add(new TermMapGroup(doc.getName()));
//        });
//        return ret;
//    }

	public PostRequestResult addGroup(String groupName) {
		PostRequestResult ret = new PostRequestResult();
		groupName = groupName.trim();
		if (groupName.length() == 0) {
			ret.errorMessage = "Invalid group Name";
			ret.status = false;
			return ret;
		}
		List<TermMapGroup> existing = getAllMappingGroups();

		for (TermMapGroup group : existing) {
			if (group.mapName.equals(groupName)) {
				// no need to add...
				ret.status = false;
				ret.errorMessage = "Group Name exists in the system";
				return ret;
			}
		}

		TermMapGroup newGroup = new TermMapGroup();
		newGroup.mapName = groupName;
		LookupDoc doc = new LookupDoc();
		doc.setId(groupName);
		doc.setName(groupName);
		doc.setType(LookupDoc.LookupType.termMapGroup);

		repository.save(doc);

		ret.status = true;
		return ret;
	}

	public PostRequestResult add(List<LookupDoc> lookupDocs) {

		PostRequestResult ret = new PostRequestResult();
		List<LookupDoc> lookupData = new ArrayList<>();
		for (LookupDoc lc : lookupDocs) {

			if (this.findOne(lc.getName(), lc.getType().toString()) != null) {
				// LOG.debug("skipping CSV data to SOLR...");
				continue;
			}

			// LOG.debug("Adding CSV data to Elastic ...");
			lookupData.add(lc);
		}
		if (lookupData.size() > 0) {

			log.debug("Begin saving to Elastic ................." + lookupData.size());
			repository.saveAll(lookupData);
		}

		ret.status = true;
		return ret;
	}

	public List<TermRuleCategory> findAllRuleCategories() {
		List<LookupDoc> serverList = findNameByType(LookupDoc.LookupType.termRuleCategory.toString());
		// List<LookupDoc> serverList =
		// solrRepository.findNameByType(LookupDoc.LookupType.termRuleCategory.toString());
		List<TermRuleCategory> ret = new ArrayList<>();
		serverList.forEach((doc) -> {
			ret.add(new TermRuleCategory(doc.getName()));
		});
		return ret;
	}

	public List<TermRulePriorityGroup> findAllRulePriorityGroups() {
		// List<LookupDoc> serverList =
		// solrRepository.findNameByType(LookupDoc.LookupType.termRulePriorityGroup.toString());
		List<LookupDoc> serverList = findNameByType(LookupDoc.LookupType.termRulePriorityGroup.toString());
		List<TermRulePriorityGroup> ret = new ArrayList<>();
		serverList.forEach((doc) -> {
			ret.add(new TermRulePriorityGroup(doc.getName()));
		});
		return ret;
	}

	public USGAAPTaxonomyElement findOneTaxonomyElement(String id) {
		// PageRequest request = new PageRequest(0, 1);
		// List<LookupDoc> serverList = solrRepository.findTaxonomyElementByName(id,
		// request);

		LookupDoc doc = this.findOne(id, LookupDoc.LookupType.taxonomyElement.toString());
		if (doc != null) {
			return new USGAAPTaxonomyElement(doc);
		}
		return null;
	}

	public List<USGAAPTaxonomyElement> findByTaxonomyElementIdLike(String query, Boolean isTextBlock) {
		//PageRequest request = new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "name_s"));
		if (query.contains("us-gaap")) {
			query = query.replace("us-gaap", "gaap");
		}

		if (query.contains("ifrs-full")) {
			query = query.replace("ifrs-full", "full");
		}

		List<LookupDoc> serverList = new ArrayList<LookupDoc>();
		try {
			// Build Query
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.must(new MatchQueryBuilder("name_s", query));
			boolQueryBuilder.must(new MatchQueryBuilder("type_s", LookupDoc.LookupType.taxonomyElement.toString()));
			boolQueryBuilder.must(new MatchQueryBuilder("isTextBlock_b", isTextBlock));

			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(boolQueryBuilder);
			// Generate the actual request to send to ES.
			SearchRequest searchRequest = new SearchRequest();
			searchRequest.source(searchSourceBuilder);
			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			serverList = getSearchResult(response);
		} catch(Exception e) {
			log.error("Elasticsearch failed: " + e.getMessage());
		}
			
		List<USGAAPTaxonomyElement> taxEles = new ArrayList<>();

		serverList.forEach((doc) -> {
			taxEles.add(new USGAAPTaxonomyElement(doc));
		});

		return taxEles;
	}
	
	public void deleteByNameAndType(String name, String type) {
		BulkByScrollResponse response =
      		  new DeleteByQueryRequestBuilder((ElasticsearchClient) client, DeleteByQueryAction.INSTANCE)
      		    .filter(QueryBuilders.matchQuery("name_s", name)) 
      		    .filter(QueryBuilders.matchQuery("type_s", type)) 
      		    .source(Indices.TERM_MAP_INFO_INDEX)                                  
      		    .get();                                             
      		long deleted = response.getDeleted();  
	}

	private List<LookupDoc> findNameByType(String type) {
		List<LookupDoc> serverList = new ArrayList<LookupDoc>();
		try {
			// Build Query
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.must(new MatchQueryBuilder("type_s", type));
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(boolQueryBuilder);
			// Generate the actual request to send to ES.
			SearchRequest searchRequest = new SearchRequest();
			searchRequest.source(searchSourceBuilder);
			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			serverList = getSearchResult(response);
		} catch (Exception e) {
			log.error("Elasticsearch failed: " + e.getMessage());
		}
		return serverList;
	}

	public LookupDoc findOne(String id, String type) {
//      PageRequest request = new PageRequest(0, 1);
//      List<LookupDoc> serverList = repository.findByNameAndType(id, type, request);
//
//      if (serverList.size() == 1) {
//          return serverList.get(0);
//      }
		// return null;
		List<LookupDoc> serverList = new ArrayList<LookupDoc>();
		try {
			// Build Query
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.must(new MatchQueryBuilder("name_s", id));
			boolQueryBuilder.must(new MatchQueryBuilder("type_s", type));

			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(boolQueryBuilder);
			// Generate the actual request to send to ES.
			SearchRequest searchRequest = new SearchRequest();
			searchRequest.source(searchSourceBuilder);
			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
			serverList = getSearchResult(response);
			if (serverList.size() == 1) {
				return serverList.get(0);
			}
		} catch (Exception e) {
			log.error("Elasticsearch failed: " + e.getMessage());
		}
		return null;
	}

	private List<LookupDoc> searchString(String field, String str) {
		// String companyName = name.toLowerCase() + "*";
		String searchStr = str;

		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.wildcardQuery(field, searchStr));

		searchSourceBuilder.query(boolQueryBuilder);
		searchRequest.source(searchSourceBuilder);

		SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return getSearchResult(response);
	}

	private List<LookupDoc> getSearchResult(SearchResponse response) {

		SearchHit[] searchHit = response.getHits().getHits();

		List<LookupDoc> docs = new ArrayList<>();

		for (SearchHit hit : searchHit) {
			docs.add(objectMapper.convertValue(hit.getSourceAsMap(), LookupDoc.class));
		}

		return docs;
	}
}
