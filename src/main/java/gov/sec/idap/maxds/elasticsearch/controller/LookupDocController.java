package gov.sec.idap.maxds.elasticsearch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.*;

import gov.sec.idap.maxds.domain.TermMapGroup;
import gov.sec.idap.maxds.domain.USGAAPTaxonomyElement;
import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.service.ReferenceDataService;

@RestController
@RequestMapping("/api/lookupdoc")
public class LookupDocController {
	private final ReferenceDataService service;
	
	@Autowired
    public LookupDocController(ReferenceDataService service) {
        this.service = service;
    }
	
//	@GetMapping(value= "/findall") 
//	List<LookupDoc> findAll () {
//		return service.findAll();
//	}
	
	//test repository query
	@GetMapping(value= "/findnamebytype") 
	List<LookupDoc> findNameByType(){
		return service.findNameByType();
	}
	
	@GetMapping(value= "/getallmappinggroups") 
	List<TermMapGroup> getAllMappingGroups(String type){
		return service.getAllMappingGroups();
	}
    
    @GetMapping(value= "/findtaxonomyelementbyname")
    USGAAPTaxonomyElement findOneTaxonomyElementByName(String name, Pageable pageable) {
    	return service.findOneTaxonomyElement(name);
    }
    
    @GetMapping(value= "/findone/{name}/{type}")
    LookupDoc findByNameAndType(@PathVariable(name = "name") String name, @PathVariable(name = "type") String type) {
    	return service.findOne(name, type);
    }
	
//    @GetMapping(value= "/findbyistextblockandnamecontaining/{istextblock}")
//    List<LookupDoc> findByIsTextBlockAndNameContaining(@PathVariable(name = "istextblock") Boolean isTextBlock) {
//    	String query = "";
//    	return service.findByTaxonomyElementIdLike(query, isTextBlock);
//    }
}