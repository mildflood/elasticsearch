package gov.sec.idap.maxds.elasticsearch.controller;

import gov.sec.idap.maxds.elasticsearch.document.TermruleDoc;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/termrule")
public class TermruleController {

    private final TermRuleService service;

    @Autowired
    public TermruleController(TermRuleService service) {
        this.service = service;
    }

    @PostMapping(value= "/save")
    public void save(@RequestBody final TermruleDoc termrule) {
        service.save(termrule);
    }

    @GetMapping("/{id}")
    public TermruleDoc findById(@PathVariable final String id) {
        return service.findById(id);
    }
    
    @PostMapping(value= "/saveall")
	public String saveAll(@RequestBody List<TermruleDoc> termrules) {
		service.saveAllTermrule(termrules);
		return "Records saved in the ES.";
	}

	@GetMapping(value= "/findall")
	public Iterable<TermruleDoc> getAll() {
		return service.findAllTermrules();
	}
	
	@GetMapping(value= "/findbyname/{name}")
	public Iterable<TermruleDoc> getByName(@PathVariable(name = "name") String name) {
		return service.findByName(name);
	}
	
	@GetMapping(value= "/findbytermid/{termid}")
	public Iterable<TermruleDoc> getBySector(@PathVariable(name = "termid") String termid) {
		return service.findByTermid(termid);
	}
}

