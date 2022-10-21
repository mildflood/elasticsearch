package gov.sec.idap.maxds.elasticsearch.controller;

import gov.sec.idap.maxds.elasticsearch.document.TermRuleDoc;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;

import java.util.List;
import java.util.Optional;

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
    public void save(@RequestBody final TermRuleDoc termrule) {
        service.save(termrule);
    }

    @GetMapping("/{id}")
    public Optional<TermRuleDoc> findById(@PathVariable final String id) {
        return service.findById(id);
    }
    
    @PostMapping(value= "/saveall")
	public String saveAll(@RequestBody List<TermRuleDoc> termrules) {
		service.saveAllTermrule(termrules);
		return "Records saved in the ES.";
	}

	@GetMapping(value= "/findall")
	public Iterable<TermRuleDoc> getAll() {
		return service.findAllTermrules();
	}
	
	@GetMapping(value= "/findbyname/{name}")
	public Iterable<TermRuleDoc> getByName(@PathVariable(name = "name") String name) {
		return service.findByName(name);
	}
	
	@GetMapping(value= "/findbytermid/{termid}")
	public Iterable<TermRuleDoc> getBySector(@PathVariable(name = "termid") String termid) {
		return service.findByTermid(termid);
	}
}

