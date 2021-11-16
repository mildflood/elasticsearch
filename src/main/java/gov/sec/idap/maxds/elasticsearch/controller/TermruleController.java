package gov.sec.idap.maxds.elasticsearch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;

@RestController
@RequestMapping("/api/termrule")
public class TermruleController {

    private final TermRuleService service;

    @Autowired
    public TermruleController(TermRuleService service) {
        this.service = service;
    }

//    @PostMapping(value= "/save")
//    public void save(@RequestBody final TermRuleDoc termrule) {
//        service.save(termrule);
//    }
//
//    @GetMapping("/{id}")
//    public TermRuleDoc findById(@PathVariable final String id) {
//        return service.findById(id);
//    }
//    
//    @PostMapping(value= "/saveall")
//	public String saveAll(@RequestBody List<TermRuleDoc> termrules) {
//		service.saveAllTermrule(termrules);
//		return "Records saved in the ES.";
//	}
//
//	@GetMapping(value= "/findall")
//	public Iterable<TermRuleDoc> getAll() {
//		return service.findAllTermrules();
//	}
//	
//	@GetMapping(value= "/findbyname/{name}")
//	public Iterable<TermRuleDoc> getByName(@PathVariable(name = "name") String name) {
//		return service.findByName(name);
//	}
//	
//	@GetMapping(value= "/findbytermid/{termid}")
//	public Iterable<TermRuleDoc> getBySector(@PathVariable(name = "termid") String termid) {
//		return service.findByTermid(termid);
//	}
}

