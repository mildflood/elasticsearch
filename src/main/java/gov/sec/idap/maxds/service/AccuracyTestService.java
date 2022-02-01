/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.domain.AccuracyTestData;
import gov.sec.idap.maxds.domain.AccuracyTestItem;
import gov.sec.idap.maxds.domain.ExportAccuracyTestInput;
import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.TermRule;
import gov.sec.idap.maxds.elasticsearch.document.AccuracyTestNotesDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermMapInformationDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.repository.AccuracyTestNotesRepository;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
import gov.sec.idap.maxds.elasticsearch.service.TermMapService;
//import gov.sec.idap.maxds.solr.document.AccuracyTestNotesDoc;
//import gov.sec.idap.maxds.solr.document.TermMapInformationDoc;
//import gov.sec.idap.maxds.solr.document.TermResultsDoc;
//import gov.sec.idap.maxds.solr.repository.AccuracyTestNotesRepository;

@Service("accuracyTestServiceSolr")
public class AccuracyTestService {
    
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final String NEW_LINE_SEPARATOR = "\n";
    
    @Autowired
    private TermRuleService termRuleService;
    
    @Autowired
    private TermResultService termResultService;
    
    @Autowired
    private TermMapService termMapService;
    
    @Autowired
    private AccuracyTestNotesRepository repository;
    
    @Autowired
    private SecApiService secApiService;

    //https://sp-us-deraodp01.ix.sec.gov:18084/maxds-filedUrl/search/findByCikAndFy?cik=1750&fy=2015
    @Value("${sec.api.idap.get.filing.url.info:https://sp-us-deraodp01.ix.sec.gov:18084/maxds-filedUrl/search/findByCikAndFy}")
    String idapFiledUrl;

    //https://sp-us-deraodp01.ix.sec.gov:18084/idap/datasets/maxds/api/v01/c951b?cik=3146&fyear=2017
    @Value("${sec.api.idap.get.compustat.data:https://sp-us-deraodp01.ix.sec.gov:18084/idap/datasets/maxds/api/v01/c951b}")
    String idapGetCompustatUrl;
    
    public AccuracyTestData runAccuracyTest(String cik, int fiscalYear) {
        
        AccuracyTestData output = new AccuracyTestData();
        output.cik = cik;
        output.fiscalYear = fiscalYear;
        output.status = true;
        
        List<TermRule> trs = this.termRuleService.getTermRulesForAccuracyTesting();
        List<String> termIds = new ArrayList<>();
        trs.forEach((tr) -> {
            termIds.add(tr.getTermId());
        });
        
        Collections.sort(trs, new Comparator<TermRule>() {
            public int compare(TermRule left, TermRule right) {
                if( left.getFinancialStatement() == null) return -1;
                if( right.getFinancialStatement() == null) return 1;
                int resp = left.getFinancialStatement().compareTo(right.getFinancialStatement()); 
                if( resp != 0) return resp;
                
                return left.getName().compareTo(right.getName());
            }
            
        });
        
        HashMap<String, TermResultsDoc> maxDSResults
                = this.termResultService.getResultsByTermId(termIds, cik, fiscalYear);
        
        HashMap<String, TermMapInformationDoc> compustatIdMaps
                = this.termMapService.findCompustatIds(termIds);
        
        HashMap<String, AccuracyTestNotesDoc> notesInfoMap
                = this.getNotesDetails(cik, fiscalYear);
        
        HashMap<String, Double> compustatValues
                = this.getCompustatValues(cik, fiscalYear, compustatIdMaps);
        
        output.items = new ArrayList<>();
        for (TermRule tr : trs) {
            
            AccuracyTestItem item = new AccuracyTestItem();
            item.termId = tr.getTermId();
            item.financialStatement = tr.getFinancialStatement();
            
            if (maxDSResults.containsKey(item.termId)) {
                item.value = maxDSResults.get(item.termId).value;
                item.resolvedExpression = maxDSResults.get(item.termId).resolvedExpression;
            }
            
            if (compustatIdMaps.containsKey(item.termId)) {
                item.csTermId = compustatIdMaps.get(item.termId).getMapTermId();
            }
            if (compustatValues.containsKey(item.termId)) {
                item.csTermValue = compustatValues.get(item.termId).doubleValue();
            }
            if (notesInfoMap.containsKey(item.termId)) {
                item.isCheckedCS = notesInfoMap.get(item.termId).getIsCheckedCS();
                item.isCheckedMaxDS = notesInfoMap.get(item.termId).getIsCheckedMaxDS();
                
                item.notes = notesInfoMap.get(item.termId).getNotes();
            }
            
            output.items.add(item);
        }
        setFilingInfo(output);
        return output;
    }

//    [ {
//
//  "gvkey" : "001224",
//
//  "cik" : 3146,
//
//  "datadate" : "20170930",
//
//  "curcd" : "USD",
//
//  "conm" : "ALABAMA GAS CORP",
//
//  "consol" : "C",
//
//  "cusip" : "010284008",
//
//  "datafmt" : "STD",
//
//  "fdate" : "20171209",
//
//  "fyear" : 2017,
//
//  "indfmt" : "INDL",
//
//  "popsrc" : "D",
//
//  "tic" : "EGN1",
//
//  "aco" : 71.3000,
//
//  "acox" : null,
//
//  "act" : 147.4000,
//
//  "am" : null,
//
//  "aoloch" : -58.7000,
//
//  "ao" : 439.6000,
//
//  "aol2" : null,
//
//  "aox" : 0.0000,
//
//  "ap" : 44.4000,   
//}]
    public HashMap<String, Double> getCompustatValues(String entityId, int fiscalYear,
            HashMap<String, TermMapInformationDoc> compustatIds) {
        HashMap<String, Double> ret = new HashMap<>();
        
        try {
            int cik = Integer.parseInt(entityId);
            
            String uri = String.format("%s?cik=%d&fyear=%d", idapGetCompustatUrl, cik, fiscalYear);
            List<LinkedHashMap<String, Object>> compustatData
                    = secApiService.getGenericJsonResponseList(uri);

            //only intereted in usd
            //"curcd" : "USD"
            for (LinkedHashMap<String, Object> dataByCur : compustatData) {
                
                String cur = (String) dataByCur.get("curcd");
                if (cur.equals("USD")) {
                    
                    for (TermMapInformationDoc doc : compustatIds.values()) {
                        
                        String key = doc.getMapTermId().toLowerCase();
                        if (dataByCur.containsKey(key)) {
                            
                            Double value = (Double) dataByCur.get(key);
                            
                            if(value != null) {
                            	value *= 1000000.0;
                                value = Math.floor(value * 100) / 100;
                            } 
                            else 
                            	value = 0.0;
                            
                            //vompustat data is rounded to millions so we need to multiply to get full amount..
                            ret.put(doc.getTermId(), value);
                        }
                    }
                }
            }
        } catch (Exception exp) {
            LOG.error("getCompustatValues: Exception  " + exp.getMessage());
        }
        
        return ret;
    }

//    {
//  "_embedded" : {
//    "maxds-filedUrl" : [ {
//      "cik" : 1750,
//      "fy" : 2015,
//      "fp" : "FY",
//      "filed" : "20150715",
//      "form" : "10-K",
//      "url" : "https://www.sec.gov/Archives/edgar/data/1750/000104746915006136/a2225345z10-k.htm",
//      "_links" : {
//        "self" : {
//          "href" : "https://sp-us-deraodp01.ix.sec.gov:18084/maxds-filedUrl/10837"
//        },
//        "filedUrl" : {
//          "href" : "https://sp-us-deraodp01.ix.sec.gov:18084/maxds-filedUrl/10837"
//        }
//      }
//    } ]
//  },
//  "_links" : {
//    "self" : {
//      "href" : "https://sp-us-deraodp01.ix.sec.gov:18084/maxds-filedUrl/search/findByCikAndFy?cik=1750&fy=2015"
//    }
//  }
//}
    public void setFilingInfo(AccuracyTestData data) {
        
        try {
            //remove leading zeros and convert to string for url query string
            
            String uri = String.format("%s?cik=%d&fy=%d", idapFiledUrl, Integer.parseInt(data.cik),
                    data.fiscalYear);
            
            LinkedHashMap<String, Object> filingData
                    = secApiService.getGenericJsonResponse(uri);

            //"_embedded"
            LinkedHashMap<String, Object> embeddedArray = (LinkedHashMap) filingData.get("_embedded");
            
            List<LinkedHashMap<String, Object>> filings = (List) embeddedArray.get("maxds-filedUrl");
            
            int cur = 0;
            for (LinkedHashMap<String, Object> filing : filings) {
//             "filed" : "20150715",
//      "form" : "10-K",
//      "url" : "https://www.sec.gov/Archives/edgar/data/1750/000104746915006136/a2225345z10-k.htm",

                String filed = (String) filing.get("filed");
                String form = (String) filing.get("form");
                String url = (String) filing.get("url");
                
                int filedInt = Integer.parseInt(filed);
                
                if (filedInt > cur) {
                    cur = filedInt;
                    
                    data.filingUrl = url;
                    data.formType = form;
                    data.filingDate = filedInt;
                }
                
            }
        } catch (Exception exp) {
            LOG.error("setFilingInfo: Exception  " + exp.getMessage());
        }
        
    }
    
    private HashMap<String, AccuracyTestNotesDoc> getNotesDetails(String entityId, int fiscalYear) {
        
        HashMap<String, AccuracyTestNotesDoc> ret = new HashMap<>();
        try {
            List<AccuracyTestNotesDoc> results = this.repository.findByEntityIdAndFYAndFQ(entityId, fiscalYear, "FY");
            
            results.forEach((doc) -> {
                ret.put(doc.getTermId(), doc);
            });
            
            return ret;
        } catch (Exception exp) {
            LOG.error("getNotesDetails: Exception  " + exp.getMessage());
        }
        
        return ret;
        
    }
    
    public void exportAccuracyTestResults(ServletOutputStream stream, ExportAccuracyTestInput input) {
        
        try {
            
            AccuracyTestData resultData = runAccuracyTest(input.cik, input.fiscalYear);
            
            List<String> header = new ArrayList();
            header.add("Company Name");
            header.add("CIK");
            header.add("Fiscal Year");
            header.add("Fiscal Period");
            header.add("CS Term ID");
            
            header.add("CS Value");
            header.add("CS Verified");
            header.add("MAXDS Term ID");
            
            header.add("MAXDS Value");
            header.add("MAXDS Resolved Expression");
            header.add("MAXDS Verified");
            header.add("Notes");
            
            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
            //LOG.debug("Header: " + header);
            csvFilePrinter.printRecord(header);
            
            for (AccuracyTestItem item : resultData.items) {
                List vals = new ArrayList();
                vals.add(input.companyName);
                vals.add(input.cik);
                vals.add(input.fiscalYear);
                vals.add(input.fiscalPeriod);
                vals.add(item.csTermId);
                
                vals.add(item.csTermValue);
                vals.add(item.isCheckedCS);
                vals.add(item.termId);
                
                vals.add(item.value);
                vals.add(item.resolvedExpression);
                vals.add(item.isCheckedMaxDS);
                vals.add(item.notes);
                
                csvFilePrinter.printRecord(vals);
            }
            streamWriter.flush();
            streamWriter.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            LOG.error("Exception raised in exportTermMapInformation. Exp Message: "
                    + e.getMessage());
        }
        
    }
    
    public PostRequestResult save(AccuracyTestData data) {
        
        List<AccuracyTestNotesDoc> docs = new ArrayList<>();
        
        for (AccuracyTestItem item : data.items) {
            
            AccuracyTestNotesDoc doc = new AccuracyTestNotesDoc();
            doc.setEntityId(data.cik);
            doc.setTermId(item.termId);
            doc.setFY(data.fiscalYear);
            doc.setFQ("FY");
            doc.setIsCheckedCS(item.isCheckedCS);
            doc.setIsCheckedMaxDS(item.isCheckedMaxDS);
            doc.setNotes(item.notes);
            doc.setId(String.format("%s-%d-%s-%s", doc.getEntityId(), doc.getFY(), doc.getFQ(), doc.getTermId()));
            docs.add(doc);
        }
        
        if (docs.size() > 0) {
            this.repository.saveAll(docs);
        }
        return PostRequestResult.GetSuccessResult();
    }
    
    private LinkedHashMap<String, Object> getGenericJsonResponse(String url, String... pathVariables) {
        
        LOG.debug("Url : " + url);
        
        HttpHeaders requestHeaders = new HttpHeaders();
        
        HttpEntity<HashMap> requestEntity = new HttpEntity<>(null, requestHeaders);
        
        RestTemplate restTemplate = new RestTemplate();
        
        ParameterizedTypeReference<LinkedHashMap> responseType
                = new ParameterizedTypeReference<LinkedHashMap>() {
        };
        
        ResponseEntity<LinkedHashMap> restResponse = restTemplate.exchange(url,
                HttpMethod.GET, requestEntity, responseType,
                (Object[]) pathVariables);
        
        return new ObjectMapper().convertValue(restResponse.getBody(), LinkedHashMap.class);
        
    }
    
}
