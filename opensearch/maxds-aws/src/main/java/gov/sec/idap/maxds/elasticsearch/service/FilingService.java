package gov.sec.idap.maxds.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.elasticsearch.document.FilingDoc;
import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;
import gov.sec.idap.maxds.elasticsearch.repository.FilingRepository;

@Service("filingService")
public class FilingService {

	@Autowired
    private FilingRepository repository;
	@Autowired
    private RestHighLevelClient client;
	@Autowired
    private ObjectMapper objectMapper;
	
    @Value("${maxds.min.fy.to.process:2016}")
    int minFYToProcess;
    @Value("${maxds.skip.fq:false}")
    Boolean skipFQProcessing;
	
	public List<FilingDoc> findFilingsByEntityIds(String cikIds) {
				
		List<FilingDoc> results = new ArrayList<FilingDoc>();

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("formType", "S-1"));
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("formType", "POS AM"));
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fy_l").gte(this.minFYToProcess));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fp", "FY"));
		}

		if (cikIds != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("entityId", Arrays.asList(cikIds.split(","))));
		}

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchRequest.source(searchSourceBuilder);

		SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		results = getSearchResult(response);
		return results;
	}

	private List<FilingDoc> getSearchResult(SearchResponse response) {
		SearchHit[] searchHit = response.getHits().getHits();
		List<FilingDoc> facts = new ArrayList<>();

		for (SearchHit hit : searchHit) {
			facts.add(objectMapper.convertValue(hit.getSourceAsMap(), FilingDoc.class));
		}
		return facts;
	}
}