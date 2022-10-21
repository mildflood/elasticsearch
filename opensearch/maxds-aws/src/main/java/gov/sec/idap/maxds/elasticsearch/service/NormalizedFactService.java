package gov.sec.idap.maxds.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.domain.ExtendedCheckResolverOutput;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;
import gov.sec.idap.maxds.elasticsearch.repository.NormalizedFactsRepository;
import gov.sec.idap.maxds.rest.client.ExtendedElementInfo;

@Service("normalizedFactService")
public class NormalizedFactService {
	@Autowired
	private RestHighLevelClient client;
	@Autowired
	private ObjectMapper objectMapper;

	//@Autowired
	//private SecApiService idapAPIService;

	@Autowired
	private NormalizedFactsRepository directFactsRepo;

	@Value("${maxds.min.fy.to.process:2016}")
	int minFYToProcess;

	@Value("${maxds.skip.fq:false}")
	Boolean skipFQProcessing;

	String corefieldsToQuery = "fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,sub_act_standard,num_ddate,sub_adsh";
// AND fact_fy_i:{%s TO *}
// AND fact_fp: "FY"

	private String getFiscalConfigurationFactQueryLimits() {

		return skipFQProcessing ? String.format("AND fact_fy_i:[%d TO *] AND fact_fp: \"FY\"", minFYToProcess)
				: String.format("AND fact_fy_i:[%d TO *]", minFYToProcess);
	}

	public List<NormalizedFact> findByEntityIdListAndElementNameIdap(String cikIds, String elementName,
			Boolean skipDimensions) {
//        String fields = skipDimensions ? corefieldsToQuery : corefieldsToQuery + ",dim_segments";
//        String query = String.format("sub_cik:%s AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4)",
//                cikIds, removePrefixfromElementName(elementName));
//        query = skipDimensions ? query + " AND -dim_segments:*" : query;
//        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
		// return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

//		String elementNameUpdated = removePrefixfromElementName(elementName);
//		List<NormalizedFact> results = directFactsRepo.findByEntityIdListAndElementNameIdap(cikIds, elementNameUpdated,
//				PageRequest.of(0, 20000, Sort.by(Sort.Direction.DESC, "sub_filed_dt")));
//https://sp-us-deraodp01.ix.sec.gov:18084/idap/datasets/generic/api/v1/Idap_FavoredFacts/select?q=sub_cik:(320193 ) AND cell_elt:CashAndDueFromBanks AND tag_custom:false AND num_qtrs:(0 1 4) AND fact_fy_i:[2016 TO *]&fl=fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,sub_act_standard,num_ddate,sub_adsh,dim_segments&sort=sub_filed_dt+desc&rows=20000
//		return results;

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
		String ele = removePrefixfromElementName(elementName);

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.FALSE));
		boolQueryBuilder.must(QueryBuilders.matchQuery("cell_elt", ele));
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(this.minFYToProcess));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		if (skipDimensions) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("dim_segments", "*"));
		}
		if (cikIds != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", Arrays.asList(cikIds.split(","))));
		}

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	public List<NormalizedFact> findByEntityIdListAndElementNameDirect(String cikIds, String elementName,
			Boolean skipDimensions) {

		// PageRequest request = new PageRequest(0, 20000, new Sort(Sort.Direction.DESC,
		// "sub_filed_dt"));
		String ele = removePrefixfromElementName(elementName);
		if (skipDimensions) {
			return directFactsRepo.findByEntityIdListAndElementNameNoDims(ele, cikIds,
					PageRequest.of(0, 20000, Sort.by(Sort.Direction.DESC, "sub_filed_dt")));
		} else {
			return directFactsRepo.findByEntityIdListAndElementName(ele, cikIds,
					PageRequest.of(0, 20000, Sort.by(Sort.Direction.DESC, "sub_filed_dt")));
		}

	}

	public List<NormalizedFact> findByEntityIdListAndElementNameWithDimensions(String cikIds, String elementName) {

//        String query = String.format("sub_cik:%s AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4) AND dim_segments:*",
//                cikIds, removePrefixfromElementName(elementName));
//        String fields = corefieldsToQuery + ",dim_segments";
//        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
		// return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

//		String ele = removePrefixfromElementName(elementName);
//		List<NormalizedFact> results = directFactsRepo.findByEntityIdListAndElementNameWithDimensions(cikIds, ele,
//				PageRequest.of(0, 20000, Sort.by(Sort.Direction.DESC, "sub_filed_dt")));
//		return results;
//		
		
		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
		String ele = removePrefixfromElementName(elementName);

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.FALSE));
		boolQueryBuilder.must(QueryBuilders.matchQuery("cell_elt", ele));
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("dim_segments", "*"));
		if (cikIds != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", Arrays.asList(cikIds.split(","))));
		}

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	public List<NormalizedFact> findValueInfoByEntityIdListAndExtendedElements(String cikIds,
			ExtendedCheckResolvedTermExpression cte) {

//        String query = null;
//        if (cte.balanceType == null || cte.balanceType == "none") {
//            query = String.format("sub_cik:%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cikIds,
//                    cte.periodType, cte.buildSolrQueryPartForElementName());
//        } else {
//            query = String.format("sub_cik:%s AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cikIds,
//                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
//        }
//
//        query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//        return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
//        if (cte.balanceType == null || cte.balanceType == "none") {
//        	results = directFactsRepo.findValueInfoByEntityIdListAndExtendedElements(cikIds, cte.periodType, cte.containsWords, cte.doesNotContainsWords, PageRequest.of(0, 20000, Sort.by(Sort.Direction.DESC, "sub_filed_dt")));
//        } else {
//        	results = directFactsRepo.findValueInfoByEntityIdListAndExtendedElements(cikIds, cte.balanceType, cte.periodType, cte.containsWords, cte.doesNotContainsWords, PageRequest.of(0, 20000, Sort.by(Sort.Direction.DESC, "sub_filed_dt")));
//        }

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(this.minFYToProcess));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}
		if (cikIds != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", Arrays.asList(cikIds.split(","))));
		}
		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);

		SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		results = getSearchResult(response);
		return results;
	}

	private List<NormalizedFact> getSearchResult(SearchResponse response) {
		SearchHit[] searchHit = response.getHits().getHits();
		List<NormalizedFact> facts = new ArrayList<>();

		for (SearchHit hit : searchHit) {
			facts.add(objectMapper.convertValue(hit.getSourceAsMap(), NormalizedFact.class));
		}
		return facts;
	}

	public List<NormalizedFact> findValueInfoByExtendedElements(ExtendedCheckResolvedTermExpression cte) {

//        String query = null;
//        if (cte.balanceType == null || cte.balanceType == "none") {
//            query = String.format("tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
//                    cte.periodType, cte.buildSolrQueryPartForElementName());
//        } else {
//            query = String.format("tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
//                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
//        }
//        query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//        return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(this.minFYToProcess));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	public List<NormalizedFact> findDimensionFactsInfoByEntityIdListAndExtendedElements(String cikIds,
			ExtendedCheckResolvedTermExpression cte) {

//		String query = null;
//		if (cte.balanceType == null || cte.balanceType == "none") {
//			query = String.format(
//					"sub_cik:%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s", cikIds,
//					cte.periodType, cte.buildSolrQueryPartForElementName());
//		} else {
//			query = String.format(
//					"sub_cik:%s AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s",
//					cikIds, cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
//		}
//		query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//		String fields = corefieldsToQuery + ",dim_segments";
//		return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}
		if (cikIds != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", Arrays.asList(cikIds.split(","))));
		}
		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	public List<NormalizedFact> findByEntityIdAndElementNameWithoutDimensions(String entityId, String elementName) {

//		Integer cik = Integer.parseInt(entityId);
//
//		String query = String.format(
//				"sub_cik:%d AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4) AND -dim_segments:*", cik,
//				removePrefixfromElementName(elementName));
//		query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//		return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
		Integer cik = Integer.parseInt(entityId);
		String ele = removePrefixfromElementName(elementName);

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.FALSE));
		boolQueryBuilder.must(QueryBuilders.matchQuery("sub_cik", cik));
		boolQueryBuilder.must(QueryBuilders.matchQuery("cell_elt", ele));

		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("dim_segments", "*"));
		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	public List<NormalizedFact> findByEntityIdAndElementNameWithDimensions(String entityId, String elementName) {

//		Integer cik = Integer.parseInt(entityId);
//
//		String query = String.format(
//				"sub_cik:%d AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4) AND dim_segments:*", cik,
//				removePrefixfromElementName(elementName));
//		String fields = corefieldsToQuery + ",dim_segments";
//		query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//		return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");
		
		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
		Integer cik = Integer.parseInt(entityId);
		String ele = removePrefixfromElementName(elementName);

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.FALSE));
		boolQueryBuilder.must(QueryBuilders.matchQuery("sub_cik", cik));
		boolQueryBuilder.must(QueryBuilders.matchQuery("cell_elt", ele));

		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("dim_segments", "*"));
		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	public List<NormalizedFact> findValueInfoByEntityIdAndExtendedElements(String entityId,
			ExtendedCheckResolvedTermExpression cte) {

//		Integer cik = Integer.parseInt(entityId);
//		String query = null;
//		if (cte.balanceType == null || cte.balanceType == "none") {
//			query = String.format(
//					"sub_cik:%d AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cik,
//					cte.periodType, cte.buildSolrQueryPartForElementName());
//		} else {
//			query = String.format(
//					"sub_cik:%d AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
//					cik, cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
//		}
//		query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//		return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
		Integer cik = Integer.parseInt(entityId);

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		boolQueryBuilder.must(QueryBuilders.matchQuery("sub_cik", cik));

		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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
	
	public List<NormalizedFact> findTextBlockFacts(String cikIds, String cell_elts) {
		List<NormalizedFact> results = new ArrayList<NormalizedFact>();

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", cikIds));

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

	public List<NormalizedFact> findDimensionFactsInfoByEntityIdAndExtendedElements(String entityId,
			ExtendedCheckResolvedTermExpression cte) {

//		Integer cik = Integer.parseInt(entityId);
//		String query = null;
//		if (cte.balanceType == null || cte.balanceType == "none") {
//			query = String.format(
//					"sub_cik:%d AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s", cik,
//					cte.periodType, cte.buildSolrQueryPartForElementName());
//		} else {
//			query = String.format(
//					"sub_cik:%d AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s",
//					cik, cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
//		}
//		query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
//		String fields = corefieldsToQuery + ",dim_segments";
//		return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

		List<NormalizedFact> results = new ArrayList<NormalizedFact>();
		Integer cik = Integer.parseInt(entityId);

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		boolQueryBuilder.must(QueryBuilders.matchQuery("sub_cik", cik));

		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_filed_dt", SortOrder.DESC);
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

	private String removePrefixfromElementName(String elementName) {

		int indexOf = elementName.indexOf('_');

		return indexOf > 0 ? elementName.substring(indexOf + 1) : elementName;
	}

	public List<ExtendedCheckResolverOutput> findMatchingExtendedElementNameInfo(Collection<String> entityIds, ExtendedCheckResolvedTermExpression cte) {
		//	String elementNameQueryInfo, String periodType, String balType) {

//		StringBuilder queryBuilder = new StringBuilder();
//
//		queryBuilder.append(String.format(
//				"%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
//				buildEntityElasticQueryStringForFacts(entityIds), periodType, getFiscalConfigurationFactQueryLimits()));
//		if (balType != null && !balType.equals("none")) {
//			queryBuilder.append(String.format(" AND tag_crdr:%s", balType));
//		}
//
//		queryBuilder.append(elementNameQueryInfo);
//
//		String fields = "cell_elt,sub_name";
//
//		List<ExtendedElementInfo> facts = idapAPIService.findExtendedElementsFacts(queryBuilder.toString(), fields,
//				"sub_name+asc");
		
		List<NormalizedFact> results = new ArrayList<NormalizedFact>();

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.mustNot(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", entityIds));

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_name", SortOrder.ASC);
		searchRequest.source(searchSourceBuilder);

		SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		results = getSearchResult(response);
		    
		List<ExtendedElementInfo> facts = new ArrayList<ExtendedElementInfo>();
		for(NormalizedFact nf : results) {
			ExtendedElementInfo eei = new ExtendedElementInfo();
			eei.companyName = nf.getSub_name();
			eei.extendedElement = nf.getCell_elt();
			eei.axisMemberName = nf.getDim_segments();
			facts.add(eei);
		}
		
		HashMap<String, ExtendedCheckResolverOutput> factsByKey = new HashMap<>();
		List<ExtendedCheckResolverOutput> ret = new ArrayList<>();
		for (ExtendedElementInfo f : facts) {
			String key = String.format("%s:%s", f.extendedElement, f.companyName);

			if (factsByKey.containsKey(key)) {
				continue;
			}
			ExtendedCheckResolverOutput item = new ExtendedCheckResolverOutput();
			item.setExtendedElement(f.extendedElement);
			item.setCompanyName(f.companyName);
			factsByKey.put(key, item);
		}

		return new ArrayList<>(factsByKey.values());
	}

	public List<ExtendedCheckResolverOutput> findMatchingExtendedElementNameInfoWithDimension( Collection<String> entityIds, ExtendedCheckResolvedTermExpression cte) {
		//	String elementNameQueryInfo, String periodType, String balType) {

//		StringBuilder queryBuilder = new StringBuilder();
//
//		queryBuilder.append(String.format(
//				"%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s",
//				buildEntityElasticQueryStringForFacts(entityIds), periodType, getFiscalConfigurationFactQueryLimits()));
//		if (balType != null && !balType.equals("none")) {
//			queryBuilder.append(String.format(" AND tag_crdr:%s", balType));
//		}
//
//		queryBuilder.append(elementNameQueryInfo);
//
//		String fields = "cell_elt,sub_name,dim_segments";
//
//		List<ExtendedElementInfo> facts = idapAPIService.findExtendedElementsFacts(queryBuilder.toString(), fields,
//				"sub_name+asc");
			
		List<NormalizedFact> results = new ArrayList<NormalizedFact>();

		SearchRequest searchRequest = new SearchRequest("maxds_favoredfacts");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_custom", Boolean.TRUE));
		if (cte.balanceType != null && cte.balanceType != "none") {
			boolQueryBuilder.must(QueryBuilders.matchQuery("tag_crdr", cte.balanceType));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("tag_iord", cte.periodType));
		for (String str : cte.containsWords) {
			boolQueryBuilder.must(QueryBuilders.wildcardQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.must(QueryBuilders.rangeQuery("fact_fy_i").gte(2016));
		if (this.skipFQProcessing) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("fact_fp", "FY"));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("dim_segments", "*"));
		for (String str : cte.doesNotContainsWords) {
			boolQueryBuilder.mustNot(QueryBuilders.matchQuery("cell_elt", "*" + str + "*"));
		}
		boolQueryBuilder.filter(QueryBuilders.termsQuery("sub_cik", entityIds));

		ArrayList<Integer> nq = new ArrayList<Integer>();
		nq.add(0);
		nq.add(1);
		nq.add(4);
		boolQueryBuilder.filter(QueryBuilders.termsQuery("num_qtrs", nq));

		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(20000);
		searchSourceBuilder.sort("sub_name", SortOrder.ASC);
		searchRequest.source(searchSourceBuilder);

		SearchResponse response = null;
		try {
			response = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		results = getSearchResult(response);
		    
		List<ExtendedElementInfo> facts = new ArrayList<ExtendedElementInfo>();
		for(NormalizedFact nf : results) {
			ExtendedElementInfo eei = new ExtendedElementInfo();
			eei.companyName = nf.getSub_name();
			eei.extendedElement = nf.getCell_elt();
			eei.axisMemberName = nf.getDim_segments();
			facts.add(eei);
		}
		
		HashMap<String, ExtendedCheckResolverOutput> factsByKey = new HashMap<>();
		List<ExtendedCheckResolverOutput> ret = new ArrayList<>();
		for (ExtendedElementInfo f : facts) {
			String key = String.format("%s:%s", f.extendedElement, f.companyName);

			if (factsByKey.containsKey(key)) {
				continue;
			}
			ExtendedCheckResolverOutput item = new ExtendedCheckResolverOutput();
			item.setExtendedElement(f.extendedElement);
			item.setCompanyName(f.companyName);
			item.setAxisMemberName(f.axisMemberName);
			factsByKey.put(key, item);
		}

		return new ArrayList<>(factsByKey.values());
	}

//    public static String buildEntitySolrQueryStringForFacts(Collection<String> entityList) {
//        //sub_cik:(51143 63908 21344 34088 310158)
//        StringBuilder bld = new StringBuilder();
//        bld.append("sub_cik:(");
//        for (String cik : entityList) {
//            bld.append(String.format("%s ", cik.replaceFirst("^0+(?!$)", "")));
//        }
//        bld.append(")");
//        return bld.toString();
//    }

	public static String buildEntityElasticQueryStringForFacts(Collection<String> entityList) {
		// sub_cik:51143,63908,21344,34088,310158
		StringBuilder bld = new StringBuilder();
		for (String cik : entityList) {
			bld.append(String.format("%s,", cik.replaceFirst("^0+(?!$)", "")));
		}
		String result = bld.toString().trim();
		result = result.substring(0, result.length() - 1); // remove comma at the end
		return result;
	}

//    public static String buildEntitySolrQueryStringForFacts(HashMap<String, Entity> entityList) {
//        //sub_cik:(51143 63908 21344 34088 310158)
//        StringBuilder bld = new StringBuilder();
//        bld.append("(");
//        for (Entity e : entityList.values()) {
//            bld.append(String.format("%s ", e.getCik().replaceFirst("^0+(?!$)", "")));
//        }
//        bld.append(")");
//        return bld.toString();
//    }

	public static String buildEntityElasticQueryStringForFacts(HashMap<String, Entity> entityList) {
		// sub_cik:51143,63908,21344,34088,310158
		StringBuilder bld = new StringBuilder();
		for (Entity e : entityList.values()) {
			bld.append(String.format("%s,", e.getCik().replaceFirst("^0+(?!$)", "")));
		}
		String result = bld.toString().trim();
		result = result.substring(0, result.length() - 1); // remove comma at the end
		return result;
	}

//	public static String buildEntitySolrQueryStringForFilings(HashMap<String, Entity> entityList) {
//		// sub_cik:(51143 63908 21344 34088 310158)
//		StringBuilder bld = new StringBuilder();
//		bld.append("(");
//		for (Entity e : entityList.values()) {
//			bld.append(String.format("%s ", e.getEntityId()));
//		}
//		bld.append(")");
//		return bld.toString();
//	}
	
	public static String buildEntityElasticQueryStringForFilings(HashMap<String, Entity> entityList) {
		// sub_cik:(51143,63908,21344,34088,310158)
		StringBuilder bld = new StringBuilder();
		for (Entity e : entityList.values()) {
			bld.append(String.format("%s,", e.getEntityId()));
		}
		String result = bld.toString().trim();
		result = result.substring(0, result.length() - 1); // remove comma at the end
		return result;
	}

}
