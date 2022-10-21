/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.FacetPivotFieldEntry;
import org.springframework.stereotype.Service;

import gov.sec.idap.maxds.elasticsearch.repository.TermResultsDocRepository;
import gov.sec.idap.maxds.solr.document.TermResultsDoc;
//import gov.sec.idap.maxds.solr.repository.TermResultsDocRepository;


@Service("solrService")
public class SolrService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
//    @Autowired
//    protected SolrTemplate solrTemplate;
    
    @Autowired
    TermResultsDocRepository termResultsDocRepository;

  
    
    
    
    //distinct count API
    
//http://184.80.156.127:8983/solr/TermResults/select?q=*:*&stats=true&stats.field=entityId&rows=0&indent=true    
//http://184.80.156.127:8983/solr/TermResults/select?q=*:*&stats=true&stats.field=entityId&rows=0&indent=true&stats.calcdistinct=true
    
    //has distinct count....
    //http://184.80.156.127:8983/solr/TermResults/select?q=myDocType:primaryTermResult%20AND%20entityId:(0000037785%20OR%200000002969%20OR%200000778164)%20AND%20FY:[2012%20TO%202015]%20AND%20FQ:FY&stats=true&stats.field=entityId&rows=0&indent=true&stats.calcdistinct=true

    public Set<String> getEntitiesTermCoverage(String termId, List<String> entityIds, int startFy, int endFy, String fq) {
        
        FacetPage<TermResultsDoc> facetPage;
        Set<String> coveredEntities = new HashSet<>();
        if (StringUtils.isBlank(fq)) {
//        	facetPage = termResultsDocRepository.findEntityFacetByTermIdEntitiesFyRange(termId, entityIds,startFy, endFy,"primaryTermResult", null);
        } else {
//        	facetPage = termResultsDocRepository.findEntityFacetByTermIdEntitiesFyRangeFQ(termId, entityIds, startFy, endFy, fq,"primaryTermResult", null);
        }
//        facetPage.getAllFacets().stream().forEach(page -> page.forEach(facet ->{ 
//            coveredEntities.add(facet.getValue());
//        }));
        
        return coveredEntities;
    }
    
    
    public Set<String> getEntitiesFacetsForTermResults(String termId, List<String> entityIds, int startFy, int endFy, String fq) {
        
        
        Set<String> coveredEntities = new HashSet<>();

        Criteria criteria = new Criteria("termId").is(termId)
                .and(new Criteria("entityId").in(entityIds))
                .and(new Criteria("FY").between(startFy, endFy));

        if (StringUtils.isNotBlank(fq)) {
            criteria.and(new Criteria("FQ").is(fq));
        }
        
        FacetQuery query = new SimpleFacetQuery(criteria)
            .setFacetOptions(new FacetOptions().addFacetOnField("entityId").setFacetLimit(1000));
        
//        FacetPage<TermResultsDoc> facetPage = solrTemplate.queryForFacetPage("TermResults", query, TermResultsDoc.class);
//        
//        facetPage.getAllFacets().stream().forEach(page -> page.forEach(facet ->{ 
//            coveredEntities.add(facet.getValue());
//        }));        
        return coveredEntities;
    }    
   
    public List<Map<String,Set<String>>> getFacetPivotsForTermResults(String termId, 
            List<String> entityIds, int startFy, int endFy, String fq, 
            String docType, String... pivots) {
        
        Map<String,Set<String>> result = new HashMap<>();
        

        Criteria criteria = new Criteria("termId").is(termId)
                .and(new Criteria("entityId").in(entityIds))
                .and(new Criteria("FY").between(startFy, endFy))
                .and(new Criteria("myDocType").is(docType));

        if (StringUtils.isNotBlank(fq)) {
            criteria.and(new Criteria("FQ").is(fq));
        }
        
        FacetQuery query = new SimpleFacetQuery(criteria)
            .setFacetOptions(new FacetOptions().addFacetOnPivot(pivots));

//        FacetPage<TermResultsDoc> facetPage = solrTemplate.queryForFacetPage("TermResults", query, TermResultsDoc.class);
//        String pivotString = String.join(",", pivots);
//        
//        List<FacetPivotFieldEntry> entries
//                = facetPage.getPivot(pivotString);

 

        List<Map<String,Set<String>>> aggregateResult = new ArrayList<>();
//        getPivotResults("", entries, result, aggregateResult);
        
        
        return aggregateResult;
    }

    private void getPivotResults (String pivotName, List<FacetPivotFieldEntry> pivots,
            Map<String,Set<String>> result, 
            List<Map<String,Set<String>>> aggregateResult ) {
        

        Set<String> entities = new HashSet<>();
         
        pivots.forEach(pivot-> {
            String pivotN = pivot.getValue();
            if (pivot.getPivot() == null || pivot.getPivot().isEmpty()) {
                entities.add(pivotN);
            } else {
                Map<String,Set<String>> nResult = new HashMap<>();
                
                if(StringUtils.isNoneBlank(pivotName)) {
                    pivotN = pivotName + "-" + pivotN;
                }
                getPivotResults(pivotN, pivot.getPivot(),nResult,aggregateResult);
                if(!nResult.isEmpty()) {
                    aggregateResult.add( nResult);
                }                
            }          
        });
        if(!entities.isEmpty()) {
            result.put(pivotName, entities);
        }
    }
}
