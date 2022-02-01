package gov.sec.idap.maxds.elasticsearch.controller;

import gov.sec.idap.maxds.elasticsearch.document.Entity;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entity")
public class EntityController {
    private final EntityService service;

    @Autowired
    public EntityController(EntityService service) {
        this.service = service;
    }

    @PostMapping(value= "/save")
    public void save(@RequestBody final Entity entity) {
        service.save(entity);
    }

    @GetMapping("/{id}")
    public Optional<Entity> findById(@PathVariable final String id) {
        return service.findById(id);
    }
    
    @PostMapping(value= "/saveall")
	public String saveAll(@RequestBody List<Entity> entities) {
		service.saveAllEntity(entities);
		return "Records saved in the ES.";
	}

	@GetMapping(value= "/findall")
	public Iterable<Entity> getAll() {
		return service.findAllEntities();
	}

	@GetMapping(value= "/findbycik/{cik}")
	public Iterable<Entity> getByCik(@PathVariable(name = "cik") String cik) {
		return service.findByCik(cik);
	}
	
	@GetMapping(value= "/findbyname/{name}")
	public Iterable<Entity> getByName(@PathVariable(name = "name") String name) {
		return service.findByName(name);
	}
	
	@GetMapping(value= "/findbysector/{name}")
	public Iterable<Entity> getBySector(@PathVariable(name = "name") String name) {
		return service.findBySector(name);
	}
	
	@GetMapping(value= "/findbyindustry/{name}")
	public Iterable<Entity> getByIndustry(@PathVariable(name = "name") String name) {
		return service.findByIndustry(name);
	}
	
	@GetMapping(value= "/findbydivision/{name}")
	public Iterable<Entity> getByDivision(@PathVariable(name = "name") String name) {
		return service.findByDivision(name);
	}
	
	@GetMapping(value= "/findbyfiler/{name}")
	public Iterable<Entity> getByFilerCategory(@PathVariable(name = "name") String name) {
		return service.findByFilerCategory(name);
	}
}
