/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.TermMapGroup;
import gov.sec.idap.maxds.domain.TermRuleCategory;
import gov.sec.idap.maxds.domain.TermRulePriorityGroup;
import gov.sec.idap.maxds.domain.USGAAPTaxonomyElement;
import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.repository.LookupDocRepository;

//import gov.sec.idap.maxds.solr.document.LookupDoc;
//import gov.sec.idap.maxds.solr.repository.LookupDocRepository;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service("referenceDataServiceSolr")
public class ReferenceDataServiceSolr {

    @Autowired
    private LookupDocRepository solrRepository;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public List<TermMapGroup> getAllMappingGroups() {
        List<LookupDoc> serverList = solrRepository.findNameByType(LookupDoc.LookupType.termMapGroup.toString());
        List<TermMapGroup> ret = new ArrayList<>();
        serverList.forEach((doc) -> {
            ret.add(new TermMapGroup(doc.getName()));
        });
        return ret;
    }

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
                //no need to add...
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

        solrRepository.save(doc);

        ret.status = true;
        return ret;
    }

   
    
     public PostRequestResult add(List<LookupDoc> lookupDocs) {

        PostRequestResult ret = new PostRequestResult();
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
            
        	log.debug("Begin saving to solr ................." + lookupData.size());
           solrRepository.saveAll(lookupData); 
        }
        
        ret.status = true;
        return ret;

    }

    public List<TermRuleCategory> findAllRuleCategories() {

        List<LookupDoc> serverList = solrRepository.findNameByType(LookupDoc.LookupType.termRuleCategory.toString());
        List<TermRuleCategory> ret = new ArrayList<>();
        serverList.forEach((doc) -> {
            ret.add(new TermRuleCategory(doc.getName()));
        });
        return ret;
    }

    public List<TermRulePriorityGroup> findAllRulePriorityGroups() {

        List<LookupDoc> serverList = solrRepository.findNameByType(LookupDoc.LookupType.termRulePriorityGroup.toString());
        List<TermRulePriorityGroup> ret = new ArrayList<>();
        serverList.forEach((doc) -> {
            ret.add(new TermRulePriorityGroup(doc.getName()));
        });
        return ret;
    }

    public USGAAPTaxonomyElement findOneTaxonomyElement(String id) {

        //PageRequest request = new PageRequest(0, 1);
    	PageRequest request = null;
        List<LookupDoc> serverList = solrRepository.findTaxonomyElementByName(id, request);

        if (serverList.size() == 1) {

            LookupDoc doc = serverList.get(0);
           // return new USGAAPTaxonomyElement(doc);
        }

        return null;
    }
    
     public LookupDoc findOne(String id, String type) {

        //PageRequest request = new PageRequest(0, 1);
    	 PageRequest request = null;
        List<LookupDoc> serverList = solrRepository.findByNameAndType(id,type, request);

        if (serverList.size() == 1) {

            return serverList.get(0);
            
        }

        return null;
    }


    public List<USGAAPTaxonomyElement> findByTaxonomyElementIdLike(String query, Boolean isTextBlock) {

        //PageRequest request = new PageRequest(0, 20, new Sort(Sort.Direction.ASC, "name_s"));
    	PageRequest request = null;
        if (query.contains("us-gaap")) {
            query = query.replace("us-gaap", "gaap");
        }
        
        if (query.contains("ifrs-full")) {
            query = query.replace("ifrs-full", "full");
        }
         
         
        List<LookupDoc> serverList = solrRepository.findByIsTextBlockAndNameContaining(isTextBlock, query, request).getContent();
        List<USGAAPTaxonomyElement> taxEles = new ArrayList<>();

        serverList.forEach((doc) -> {
           // taxEles.add(new USGAAPTaxonomyElement(doc));
        });

        return taxEles;

    }

}
