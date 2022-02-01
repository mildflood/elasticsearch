/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.elasticsearch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.domain.SECFilings;
import gov.sec.idap.maxds.domain.TextBlockInfo;
import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;
import gov.sec.idap.maxds.rest.client.ExtendedElementInfo;
import gov.sec.idap.maxds.rest.client.IdapGenericApiResult;
import gov.sec.idap.maxds.rest.client.SecRestClient;

/**
 *
 * @author srira
 */
@Service
public class SecApiService {

    
  
       
    @Value("${sec.api.idap.root:http://localhost:18184/idap/datasets/generic/api/v1/}")
    String idapRoot;
    
    @Value("${sec.api.idap.entity.core:EntityReferences}")
    String entityCore;
    
     @Value("${sec.api.idap.filings.core:SECFilings}")
    String filingsCore;
     
      @Value("${sec.api.idap.facts.core:SECNormalizedFacts}")
    String factsCore;
    
       @Value("${maxds.skip.fq:true}")
    Boolean skipFQProcessing;
      
    int rowCount = 20000;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    public LinkedHashMap<String, Object> getGenericJsonResponse(String url, String... pathVariables) {

        LOG.debug("Url : " + url);
      
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<LinkedHashMap> requestEntity = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<LinkedHashMap> responseType
                = new ParameterizedTypeReference<LinkedHashMap>() {
        };

        ResponseEntity<LinkedHashMap> restResponse = restTemplate.exchange(url,
                HttpMethod.GET, requestEntity, responseType,
                (Object[]) pathVariables);
        
        
        return new ObjectMapper().convertValue(restResponse.getBody(), LinkedHashMap.class);

    }
    public List<LinkedHashMap<String, Object>> getGenericJsonResponseList(String url, String... pathVariables) {

        LOG.debug("Url : " + url);
      
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<List> requestEntity = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<List> responseType
                = new ParameterizedTypeReference<List>() {
        };

        ResponseEntity<List> restResponse = restTemplate.exchange(url,
                HttpMethod.GET, requestEntity, responseType,
                (Object[]) pathVariables);
        
        
        return new ObjectMapper().convertValue(restResponse.getBody(), List.class);

    }
    

   public List<TextBlockInfo> findTextBlockFacts( String query, String fields 
              )
    {
        SecRestClient<TextBlockInfo> client = new SecRestClient<>();
        
        String url =  String.format("%s%s/select?q=%s&fl=%s&rows=%d",
                idapRoot,factsCore, query,fields, rowCount   );
        return  client.doGetListFromIdap(TextBlockInfo.class, url);
    }
    
    public List<NormalizedFact> findFacts( String query, String fields, 
            String sort  )
    {
        SecRestClient<NormalizedFact> client = new SecRestClient<>();
        
        String url =  String.format("%s%s/select?q=%s&fl=%s&sort=%s&rows=%d",
                idapRoot,factsCore, query,fields, sort,rowCount   );
        return  client.doGetListFromIdap(NormalizedFact.class, url);
    }
    
    public List<ExtendedElementInfo> findExtendedElementsFacts( String query, String fields, 
             String sort  )
    {
        SecRestClient<ExtendedElementInfo> client = new SecRestClient<>();
        
        String url =  String.format("%s%s/select?q=%s&fl=%s&rows=%d&sort=%s",
                idapRoot,factsCore, query,fields, rowCount ,sort  );
        return  client.doGetListFromIdap(ExtendedElementInfo.class, url);
    }
    
    public List<SECFilings> findFilingsByQuery(String query){
        
         SecRestClient<SECFilings> client = new SecRestClient<>();
        
        String url =  String.format("%s%s/select?rows=%d&q=%s&fl=entityId,filingPeriod,fiscalYearEnd,formType,fy_l,fp",
                idapRoot,filingsCore,rowCount, query );
        return  client.doGetListFromIdap(SECFilings.class, url);
      
    }
    
    public List<Entity> getAllEntities(String fields) {

        SecRestClient<Entity> client = new SecRestClient<>();
        
        String url =  fields == null ?
                String.format("%s%s/select?rows=%d", idapRoot,entityCore, rowCount ):
                String.format("%s%s/select?rows=%d&fl:%s", idapRoot,entityCore,rowCount, fields );
         
        return client.doGetListFromIdap(Entity.class, url);
    }
    

   
    
    public List<Entity> getEntitiesByQuery(String query) {

        SecRestClient<Entity> client = new SecRestClient<>();
        
        String url = String.format("%s%s/select?q=%s&rows=%d", idapRoot,
                entityCore, query,rowCount );
       return client.doGetListFromIdap(Entity.class, url);
    }
    
    public List<Entity> getEntityFieldsByQuery(String query,String fields) {

        SecRestClient<Entity> client = new SecRestClient<>();
        
        String url = String.format("%s%s/select?q=%s&fl=%s&rows=%d", idapRoot,
                entityCore, query, fields,rowCount );
        return client.doGetListFromIdap(Entity.class, url);
    }
    
   
    
    
     public HashMap<String, Boolean> getEntitiesForYear(int year)
    {
        HashMap<String, Boolean> ret = new HashMap<>();
        SecRestClient<Entity> client = new SecRestClient<>();
        
        String fqQuery = skipFQProcessing ? "%20AND%20fp=FY":"";
      
        String url = String.format("%s%s/select?q=fy_l:%d%s&facetParams=facet.field=cik%s&rows=0", idapRoot,
                filingsCore, year,fqQuery, "%26facet=on%26facet.mincount=1%26facet.limit=15000");
        IdapGenericApiResult result =  client.getIdapResult(url);
        
         Map<String, Object> fp 
                 = ( Map<String, Object>)result.facet_counts.get("facet_fields");
         
         List facetData = (ArrayList<Object>) fp.get("cik");
         
         for( int i = 0 ; i < facetData.size() ; i=i+2){
             
             ret.put(facetData.get(i).toString(), Boolean.FALSE);
         }
         
        return  ret;
         
      
    }

   //http://158.85.220.10:18184/idap/datasets/generic/api/v1/Idap_Filings/select?q=fy_l:2014&facetParams="facet.pivot=cik%26facet=on%26facet.mincount=1"&rows=0
    //http://158.85.220.10:18184/idap/datasets/generic/api/v1/Idap_Filings/select?q=*:*&facetParams=facet.pivot=fy_l,cik%26facet=on%26facet.mincount=1&rows=0

//http://158.85.220.10:18184/idap/datasets/generic/api/v1/Idap_FavoredFacts/select?q=sub_cik:(51143 OR 51144 OR 19617) AND tag_custom:true AND tag_iord:I&facetParams=facet.pivot=sub_cik,cell_elt%26facet=on%26facet.mincount=1&rows=0

    
    
    //

}
