package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;

@Repository
public interface TermResultsDocRepository extends ElasticsearchRepository<TermResultsDoc, String> {

    public void deleteByTermId(String termRuleId);

    public void deleteByTermName(String termName);

    public void deleteByTermIdAndEntityId(String termRuleId, String entityId);

    public List<TermResultsDoc> findByTermIdAndMyDocType(String termId, String myDocType);

    public List<TermResultsDoc> findByEntityIdAndMyDocType(String entityId, String myDocType);

    public List<TermResultsDoc> findByEntityIdAndTermNameAndMyDocType(String entityId, String name, String myDocType);
    
     //@Query(value = "entityId:?0 AND termId:?1 AND myDocType:?2")
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"entityId\" : \"?0\" }},{\"match\" : { \"termId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"?2\" }}]}}")
    public List<TermResultsDoc> findByEntityIdListAndTermIdAndMyDocType(String entityIds, String termId, String myDocType);

    public List<TermResultsDoc> findByEntityIdAndTermIdAndMyDocType(String entityId, String termId, String myDocType);

    //  @Query(value="termId:?0&rank:[* TO ?1]&myDocType:primaryTermResult")
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}, {\"range\": {\"rank\": {\"lte\": \"?1\"}}}]}}")
    public List<TermResultsDoc> findByTermIdAndMyDocTypeAndRankLessThanEqual(String termId,
            String myDocType, int rank);

    //@Query(value = "termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND myDocType:primaryTermResult")
//    @Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}]}}")
    
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "      ], \r\n"
    		+ "      \"filter\": { \"bool\": { \"should\": [\r\n"
    		+ "                { \"terms\":  { \"termId\": ?0 }},\r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "      ]}}\r\n"
    		+ "    }\r\n"
    		+ "  }")
    //@Query("{ \"bool\": { \"must\": [ {\"match\" : { \"myDocType\" : \"primaryTermResult\" }}, {\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]} }")
    public List<TermResultsDoc> findByTermsInAndEntitiesInAndFYBetween(Collection<String> termIds,
            Collection<String> entityIds, int start, int end);
//      public List<TermResultsDoc> findByTermIdInAndEntityIdInAndFYBetween(Collection<String> termIds,
//              Collection<String> entityIds, int start, int end);
    
    //@Query(value = "termId:(?0) AND entityId:?1 AND FY:?2 AND FQ:?3 AND myDocType:primaryTermResult")
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"?2\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}]}}")
    @Query("{\r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n" 
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"match\" : { \"entityId\" : \"?1\" }}, \r\n"
    		+ "		{\"match\": {\"FY\": \"?2\" }},\r\n"
    		+ "		{\"match\": {\"FQ\": \"?3\" }}\r\n"
    		+ "      ], \r\n"
    		+ "      \"filter\": [\r\n"
    		+ "         { \"terms\":  { \"termId\": ?0 }}\r\n"
    		+ "	  ]\r\n"
    		+ "    }\r\n"
    		+ "  }")
    public List<TermResultsDoc> findByTermsInAndEntityIdAndFYAndFQ(Collection<String> termIds,
            String entityId, int fy, String fq);
    
    //@Query(value = "termId:(?0) AND FY:[?1 TO ?2] AND myDocType:primaryTermResult")
   // @Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}, {\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}\r\n"
    		+ "      ], \r\n"
    		+ "      \"filter\":[\r\n"
    		+ "                { \"terms\":  { \"termId\": ?0 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  }")
    public List<TermResultsDoc> findByTermsAndFY(Collection<String> termIds, int start, int end);

    //@Query(value = "termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND -FQ:FY AND myDocType:primaryTermResult")
    //@Query(" {\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}, {\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}}")
    @Query("{\r\n"
    		+ "  \"query\": { \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "      ], \r\n"
    		+ "	  \"must_not\": [\r\n"
    		+ "		{\"match\": { \"FQ\" : \"FY\" }}\r\n"
    		+ "	  ]\r\n"
    		+ "      \"filter\": { \"bool\": { \"should\": [\r\n"
    		+ "                { \"terms\":  { \"termId\": ?0 }},\r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "      ]}}\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "}")
    public List<TermResultsDoc> findByTermsEntitiesAndFYQuarterlyOnly(Collection<String> termIds,
            Collection<String> entityIds, int start, int end);

    //@Query(value = "termId:(?0) AND FY:[?1 TO ?2] AND -FQ:FY AND myDocType:primaryTermResult")
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}, {\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}], \"must_not\":[{\"match\" : { \"FQ\" : \"FY\" }}]}}")
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}\r\n"
    		+ "      ], \r\n"
    		+ "	  \"must_not\": [\r\n"
    		+ "		{\"match\": { \"FQ\" : \"FY\" }}\r\n"
    		+ "	  ]\r\n"
    		+ "      \"filter\": [\r\n"
    		+ "                { \"terms\":  { \"termId\": ?0 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  }")
    public List<TermResultsDoc> findByTermsAndFYQuarterlyOnly(Collection<String> termIds, int start, int end);

    //@Query(value = "termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:FY AND myDocType:primaryTermResult")
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }},{\"match\" : {\"FQ\":\"FY\"}},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}}")
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"match\" : { \"FQ\" : \"FY\" }}, \r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "      ], \r\n"
    		+ "      \"filter\": { \"bool\": { \"should\": [\r\n"
    		+ "                { \"terms\":  { \"termId\": ?0 }},\r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "      ]}}\r\n"
    		+ "    }\r\n"
    		+ "  }")
    public List<TermResultsDoc> findByTermsEntitiesAndFYYealyOnly(Collection<String> termIds,
            Collection<String> entityIds, int start, int end);

    //@Query(value = "termId:(?0) AND FY:[?1 TO ?2] AND FQ:FY AND myDocType:primaryTermResult")
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }},{\"match\" : {\"FQ\":\"FY\"}},{\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"primaryTermResult\" }},  \r\n"
    		+ "		{\"match\" : { \"FQ\" : \"FY\" }}, \r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}\r\n"
    		+ "      ], \r\n"
    		+ "      \"filter\": [\r\n"
    		+ "                { \"terms\":  { \"termId\": ?0 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  }")
    public List<TermResultsDoc> findByTermsAndFYYealyOnly(Collection<String> termIds,
            int start, int end);

    //@Query(value = "termId:?0 AND entityId:?1 AND FY:[?2 TO ?3] AND myDocType:primaryTermResult")
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}}")
    public List<TermResultsDoc> findByTermIdEntityIdAndFY(String termId,
            String entityId, int start, int end);

    //@Query(value = "termId:?0 AND entityId:?1 AND FY:[?2 TO ?3] AND -FQ:FY AND myDocType:primaryTermResult")
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }}, {\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}], \"must_not\":[{\"match\" : { \"FQ\" : \"FY\" }}]}}")
    public List<TermResultsDoc> findByTermIdEntityIdAndFYQuarterlyOnly(String termId,
            String entityId, int start, int end);

    //@Query(value = "termId:?0 AND entityId:?1 AND FY:[?2 TO ?3] AND FQ:FY AND myDocType:primaryTermResult")
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"myDocType\" : \"primaryTermResult\" }},{\"match\" : {\"FQ\":\"FY\"}},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}}")
    public List<TermResultsDoc> findByTermIdEntityIdAndFYYealyOnly(String termId,
            String entityId, int start, int end);

//    @Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4)")
//    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
   // @Query("{\"query\":{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?5\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"?4\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}}")
    @Query("{ \r\n"
     		+ "    \"bool\": {\r\n"
     		+ "      \"must\": [\r\n"
     		+ "        	{\"match\" : { \"myDocType\" : \"?5\" }},  \r\n"
     		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
     		+ "			{\"match\" : { \"FQ\" : \"?4\" }},\r\n"
     		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
     		+ "      ],\r\n"
     		+ "      \"filter\": [ \r\n"
     		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
     		+ "      ]\r\n"
     		+ "    }\r\n"
     		+ "  },\r\n"
     		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeFQ(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, Pageable pageable);
    
    //@Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4) AND validationStatus:InValid")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?5\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"?4\" }},{\"match\" : { \"validationStatus\" : \"InValid\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}")
    @Query("{ \r\n"
     		+ "    \"bool\": {\r\n"
     		+ "      \"must\": [\r\n"
     		+ "        	{\"match\" : { \"myDocType\" : \"?5\" }},  \r\n"
     		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
     		+ "			{\"match\" : { \"FQ\" : \"?4\" }},\r\n"
     		+ "			{\"match\" : { \"validationStatus\" : \"InValid\" }}, \r\n " 
     		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
     		+ "      ],\r\n"
     		+ "      \"filter\": [ \r\n"
     		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
     		+ "      ]\r\n"
     		+ "    }\r\n"
     		+ "  },\r\n"
     		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeFQWithValError(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, Pageable pageable);

    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        {\"match\" : { \"myDocType\" : \"?4\" }},  \r\n"
    		+ "		{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "		{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "      ],\r\n"
    		+ "      \"filter\": [ \r\n"
    		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  },\r\n"
    		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRange(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);
    
    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND validationStatus:InValid")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    //@Query("{\"query\":{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"validationStatus\" : \"InValid\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}}")
    @Query("{ \r\n"
     		+ "    \"bool\": {\r\n"
     		+ "      \"must\": [\r\n"
     		+ "        	{\"match\" : { \"myDocType\" : \"?4\" }},  \r\n"
     		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
     		+ "			{\"match\" : { \"validationStatus\" : \"InValid\" }}, \r\n " 
     		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
     		+ "      ],\r\n"
     		+ "      \"filter\": [ \r\n"
     		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
     		+ "      ]\r\n"
     		+ "    }\r\n"
     		+ "  },\r\n"
     		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeWithValError(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);
    
    //@Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4) AND rank:(?6)")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    //@Query("{\"query\":{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?5\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"?4\" }},{\"match\" : { \"rank\" : \"?6\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}}")
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        	{\"match\" : { \"myDocType\" : \"?5\" }},  \r\n"
    		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "			{\"match\" : { \"FQ\" : \"?4\" }},\r\n"
    		+ "			{\"match\" : { \"rank\" : \"?6\" }},\r\n"
    		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "      ],\r\n"
    		+ "      \"filter\": [ \r\n"
    		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  },\r\n"
    		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeFQRank(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, int rank, Pageable pageable);
    
     //@Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4) AND rank:(?6) AND validationStatus:InValid")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
     //@Query("{\"query\":{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?5\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"?4\" }},{\"match\" : { \"rank\" : \"?6\" }},{\"match\" : { \"validationStatus\" : \"InValid\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}}")
     @Query("{ \r\n"
     		+ "    \"bool\": {\r\n"
     		+ "      \"must\": [\r\n"
     		+ "        	{\"match\" : { \"myDocType\" : \"?5\" }},  \r\n"
     		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
     		+ "			{\"match\" : { \"FQ\" : \"?4\" }},\r\n"
     		+ "			{\"match\" : { \"rank\" : \"?6\" }},\r\n"
     		+ "			{\"match\" : { \"validationStatus\" : \"InValid\" }}, \r\n " 
     		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
     		+ "      ],\r\n"
     		+ "      \"filter\": [ \r\n"
     		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
     		+ "      ]\r\n"
     		+ "    }\r\n"
     		+ "  },\r\n"
     		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
     public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeFQRankWithValError(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, int rank, Pageable pageable);

    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND rank:(?5)")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    //@Query("{\"query\":{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"rank\" : \"?5\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}}")
    @Query("{ \r\n"
    		+ "    \"bool\": {\r\n"
    		+ "      \"must\": [\r\n"
    		+ "        	{\"match\" : { \"myDocType\" : \"?4\" }},  \r\n"
    		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "			{\"match\" : { \"rank\" : \"?5\" }},\r\n"
    		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "      ],\r\n"
    		+ "      \"filter\": [ \r\n"
    		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "      ]\r\n"
    		+ "    }\r\n"
    		+ "  },\r\n"
    		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeRank(String termId, Collection<String> entityIds,
            int start, int end, String type, int rank, Pageable pageable);

     // @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND rank:(?5) AND validationStatus:InValid")
    //@Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    //@Query("{\"query\":{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"rank\" : \"?5\" }},{\"match\" : { \"validationStatus\" : \"InValid\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}}}")
    @Query("{ \r\n"
     		+ "    \"bool\": {\r\n"
     		+ "      \"must\": [\r\n"
     		+ "        	{\"match\" : { \"myDocType\" : \"?4\" }},  \r\n"
     		+ "			{\"match\" : { \"termId\" : \"?0\" }},\r\n"
     		+ "			{\"match\" : { \"rank\" : \"?5\" }},\r\n"
     		+ "			{\"match\" : { \"validationStatus\" : \"InValid\" }}, \r\n " 
     		+ "			{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
     		+ "      ],\r\n"
     		+ "      \"filter\": [ \r\n"
     		+ "        { \"terms\":  { \"entityId\": ?1 }}\r\n"
     		+ "      ]\r\n"
     		+ "    }\r\n"
     		+ "  },\r\n"
     		+ "  \"aggs\": {\"entityId\": {\"terms\":{\"field\":\"entityId\",\"min_doc_count\":1, \"size\":1000}}} ")
    public SearchPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFYRangeRankWithValError(String termId, Collection<String> entityIds,
            int start, int end, String type, int rank, Pageable pageable);
    

    
    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]")
    //@Facet(pivots = @Pivot({"rank", "entityId"}), limit = 15000)
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"rank_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"rank\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    @Query("{\r\n"
    		+ "		\"bool\": {\r\n"
    		+ "			\"must\":[\r\n"
    		+ "				{\"match\" : { \"myDocType\" : \"?4\" }},\r\n"
    		+ "				{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "				{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "			],\r\n"
    		+ "			\"filter\": [ \r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "			]\r\n"
    		+ "		}\r\n"
    		+ "	},\r\n"
    		+ "	\"aggs\": {\"rank_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"rank\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    public SearchPage<TermResultsDoc> findEntityRankPivotFacetByTermIdEntitiesFYRange(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]  AND FQ:FY")
    //@Facet(pivots = @Pivot({"rank", "entityId"}), limit = 15000)
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"FY\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"rank_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"rank\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    @Query("{\r\n"
    		+ "		\"bool\": {\r\n"
    		+ "			\"must\":[\r\n"
    		+ "				{\"match\" : { \"myDocType\" : \"?4\" }},\r\n"
    		+ "				{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "				{\"match\" : { \"FQ\" : \"FY\" }},\r\n"
    		+ "				{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "			],\r\n"
    		+ "			\"filter\": [ \r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "			]\r\n"
    		+ "		}\r\n"
    		+ "	},\r\n"
    		+ "	\"aggs\": {\"rank_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"rank\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    public SearchPage<TermResultsDoc> findEntityRankPivotFacetByTermIdEntitiesFYRangeFQ(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]")
    //@Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"fy_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"FY\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    @Query("{\r\n"
    		+ "		\"bool\": {\r\n"
    		+ "			\"must\":[\r\n"
    		+ "				{\"match\" : { \"myDocType\" : \"?4\" }},\r\n"
    		+ "				{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "				{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "			],\r\n"
    		+ "			\"filter\": [ \r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "			]\r\n"
    		+ "		}\r\n"
    		+ "	},\r\n"
    		+ "	\"aggs\": {\"fy_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"FY\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    public SearchPage<TermResultsDoc> findEntityYearPivotFacetByTermIdEntitiesFYRange(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    //@Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:FY")
    //@Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    //@Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?4\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"entityId\" : \"?1\" }},{\"match\" : { \"FQ\" : \"FY\" }},{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}},\"aggs\": {\"fy_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"FY\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    @Query("{\r\n"
    		+ "		\"bool\": {\r\n"
    		+ "			\"must\":[\r\n"
    		+ "				{\"match\" : { \"myDocType\" : \"?4\" }},\r\n"
    		+ "				{\"match\" : { \"termId\" : \"?0\" }},\r\n"
    		+ "				{\"match\" : { \"FQ\" : \"FY\" }},\r\n"
    		+ "				{\"range\": {\"FY\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}\r\n"
    		+ "			],\r\n"
    		+ "			\"filter\": [ \r\n"
    		+ "				{ \"terms\":  { \"entityId\": ?1 }}\r\n"
    		+ "			]\r\n"
    		+ "		}\r\n"
    		+ "	},\r\n"
    		+ "	\"aggs\": {\"fy_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"FY\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    public SearchPage<TermResultsDoc> findEntityYearPivotFacetByTermIdEntitiesFYRangeFQ(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    //@Query(value = "myDocType:(?3) AND termId:(?0) AND FY:[?1 TO ?2]")
    //@Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?3\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}},\"aggs\": {\"fy_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"FY\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    public SearchPage<TermResultsDoc> findAllEntityYearPivotFacetByTermIdEntitiesFYRange(String termId,
            int start, int end, String type, Pageable pageable);

    //@Query(value = "myDocType:(?3) AND termId:(?0) AND FY:[?1 TO ?2] AND FQ:FY")
    //@Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"myDocType\" : \"?3\" }},{\"match\" : { \"termId\" : \"?0\" }},{\"match\" : { \"FQ\" : \"FY\" }},{\"range\": {\"FY\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}},\"aggs\": {\"fy_entityId\": {\"multi_terms\":{\"terms\":[{\"field\":\"FY\"},{\"field\":\"entityId\"}],\"size\":15000}}}")
    public SearchPage<TermResultsDoc> findAllEntityYearPivotFacetByTermIdEntitiesFYRangeFQ(String termId,
            int start, int end, String type, Pageable pageable);
}
