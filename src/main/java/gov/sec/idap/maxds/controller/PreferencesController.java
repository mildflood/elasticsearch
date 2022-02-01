package gov.sec.idap.maxds.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import gov.sec.idap.maxds.domain.TermRuleProcessing;
import gov.sec.idap.maxds.model.FeedbackForm;
import gov.sec.idap.maxds.model.Profile;
import gov.sec.idap.maxds.model.TermNames;
import gov.sec.idap.maxds.service.ProcessingService;
import gov.sec.idap.maxds.services.PreferencesServices;
import gov.sec.idap.maxds.util.ProfileProcessUtils;
import gov.sec.idap.maxds.util.Util;
import gov.sec.prototype.edm.domain.Preferences;

@CrossOrigin
@RestController
public class PreferencesController {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PreferencesServices preferencesServices;
	
	@Autowired
    private ProcessingService processingService;
	
	 @Autowired
	    private ProfileProcessUtils processUtils;
	 
	 @Autowired
	 ServletContext servletContext;
	 
	@Autowired
	private Util util;
	    
	 @RequestMapping(value = "/sendFeedback", method = RequestMethod.POST)
	    public void sendFeedback(@RequestBody FeedbackForm form) {
	        LOG.debug("sendFeedback: ");
	        SecurityContextHolder.getContext().getAuthentication();
	        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	        HttpSession session = attr.getRequest().getSession(true);
	        form.setUser(form.getEmail().split("@")[0]);
	        preferencesServices.saveFeedback(form);
	    }
	 
	@CrossOrigin
	@RequestMapping(value ="/listPreferences/{userid}", method = RequestMethod.GET)
	public @ResponseBody List<Preferences> listPreferences(@PathVariable String userid){
		//System.out.println("listPreferences Started");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String  name = userid;//servletContext.getAttribute("username").toString();authentication.getName();
		LOG.info("listPreferences Started for "+name);
		List<Preferences> ret =  preferencesServices.fetchPreferences(name);
		System.out.println("listPreferences End "+ret.size());
		return ret;
	}
	
	@CrossOrigin
	@RequestMapping(value ="/listSharedPreferences/{userid}", method = RequestMethod.GET)
	public @ResponseBody List<Preferences> listSharedPreferences(@PathVariable String userid){
		//System.out.println("listPreferences Started");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String  name = userid;//servletContext.getAttribute("username").toString();authentication.getName();
		LOG.info("listPreferences Started for "+name);
		List<Preferences> ret =  preferencesServices.fetchSharedPreferences(name);
		System.out.println("listPreferences End "+ret.size());
		return ret;
	}
	
	@GetMapping(value = "/EntitiesList")
    public @ResponseBody List<String> getAllEntities() {
        List<String> ret = preferencesServices.getAllEntities();
        return ret;
    }
	
	@GetMapping(value = "/DivisionList")
    public @ResponseBody List<String> getAllDivisions() {
        List<String> ret = preferencesServices.getAllDivisions();
        return ret;
    }
	
	@GetMapping(value = "/SectorList")
    public @ResponseBody List<String> getAllSectors() {
        List<String> ret = preferencesServices.getAllSectors();
        return ret;
    }
	
	@GetMapping(value = "/IndustryList")
    public @ResponseBody List<String> getAllIndustries() {
        List<String> ret = preferencesServices.getAllIndustries();
        return ret;
    }
	
	@GetMapping(value = "/FilerCategory")
    public @ResponseBody List<String> getAllFilerCategories() {
        List<String> ret = preferencesServices.getAllFilerCategories();
        return ret;
    }
	
	@GetMapping(value = "/DivSectorList")
    public @ResponseBody List<String> getAllDivisionSector() {
        List<String> ret = preferencesServices.getAllDivisionSectors();
        return ret;
    }
	
	 @CrossOrigin
		@RequestMapping("/PeerDetails")
	    public 
	    Object getPeerDetails(@RequestParam String companyName) {
		 List<Map<String, Object>> ret = preferencesServices.getPeerDetails(companyName);
			return ret;
	 }
	
	@GetMapping(value = "/termNamesList")
    public @ResponseBody List<TermNames> getAllTermNames() {
		System.out.println(" Termnames List >> ");
        SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        List<TermNames> ret = preferencesServices.fetchTermNames();
        System.out.println(" Termnames >> End" + ret.size());
        return ret;
    }
	
	public String formString(String[] companies) {
		String results="";
		for(String company:companies) {
			results = results+company.concat(",");
		}
		return results.substring(0, (results.length()-1));
	}
	
	@PostMapping(value="/savePreferences")
	public @ResponseBody List<Preferences> savePreferences(@RequestBody Profile preferences) {
		/* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 String  name = authentication.getName();
	     ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();*/
		 String  name = preferences.getUserid();//servletContext.getAttribute("username").toString();
		 System.out.println("User ID  : "+preferences.getUserid()+" savePreferences >> "+preferences.getCompanyName() +" >> "+preferences.getTermName() +" >> " +preferences.getPreName());
		 Preferences preferences2 = new Preferences();
		 String code ="";
		 if(preferences.getTermName()!= "") {
			  code = preferences.getTermName().split("[\\(\\)]")[1];
		 }else {
			 code ="**All Terms**";
		 }
		 preferences2.setTermName(preferences.getTermName());
		 preferences2.setCode(code);
		 preferences2.setCompanyName(preferences.getCompanyName());
		 preferences2.setQuaterly("YES");
		 preferences2.setUserid(name);
		 preferences2.setValidationStatus("N/A");
		 preferences2.setResultLink("N/A");
		 preferences2.setFsqvLink("N/A");
		 preferences2.setPreferenceName(preferences.getPreName());
		 preferencesServices.savePreferences(preferences2);
		 List<Preferences> ret =  preferencesServices.fetchPreferences(name);
		 System.out.println("listPreferences End "+ret.size());
		return ret; 
	}
	
	@PostMapping(value="/saveSharedPreferences/{code}/{userid}")
	public @ResponseBody List<Preferences> saveSharedPreferences(@RequestBody List<Profile> preferencesList,@PathVariable String code,@PathVariable String userid) {
		/* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 String  name = authentication.getName();
	     ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();*/
		//servletContext.getAttribute("username").toString();
		
		
		for(Profile preferences: preferencesList)
		{
			boolean isAvailable = false;
			List<Preferences> pref3 =  preferencesServices.fetchSharedPreferences(preferences.getUserid());
			for(Preferences p1:pref3)
			{
				if(preferences.getCompanyName().trim().equalsIgnoreCase(p1.getCompanyName().trim())
						&& preferences.getTermName().trim().equalsIgnoreCase(p1.getTermName().trim())
						&& preferences.getPreName().trim().equalsIgnoreCase(p1.getPreferenceName().trim()))
				{
					isAvailable = true;
				}
			}
			if(!isAvailable)
			{
				Preferences preferences2 = new Preferences();
				 preferences2.setTermName(preferences.getTermName());
				 preferences2.setCode(code);
				 preferences2.setCompanyName(preferences.getCompanyName());
				 preferences2.setQuaterly("YES");
				 preferences2.setUserid(preferences.getUserid());
				 preferences2.setValidationStatus("N/A");
				 preferences2.setResultLink("N/A");
				 preferences2.setFsqvLink("N/A");
				 preferences2.setPreferenceName(preferences.getPreName());
				 preferencesServices.saveSharedPreferences(preferences2);
			}
		}
		 List<Preferences> ret =  preferencesServices.fetchSharedPreferences(userid);
		 System.out.println("listPreferences End "+ret.size());
		return ret; 
	}
	
	@PostMapping(value="/deletePreferences/{userid}")
	public @ResponseBody List<Preferences> deletePreferences(@RequestBody List<Integer> ids,@PathVariable String userid) {
		 System.out.println(" deletePreferences List >> "+ids);
		/* SecurityContextHolder.getContext().getAuthentication();
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 String  name = servletContext.getAttribute("username").toString();//authentication.getName();
	     ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();*/
	     String  name = userid;//servletContext.getAttribute("username").toString();
		 preferencesServices.deletePreferences(ids);
		 List<Preferences> ret =  preferencesServices.fetchPreferences(name);
		 System.out.println("listPreferences End "+ret.size());
		return ret; 
	}
	
	@PostMapping(value="/deleteSharedPreferences/{userid}")
	public @ResponseBody List<Preferences> deleteSharedPreferences(@RequestBody List<Integer> ids,@PathVariable String userid) {
		 System.out.println(" deleteSharedPreferences List >> "+ids);
		/* SecurityContextHolder.getContext().getAuthentication();
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 String  name = servletContext.getAttribute("username").toString();//authentication.getName();
	     ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();*/
	     String  name = userid;//servletContext.getAttribute("username").toString();
		 preferencesServices.deleteSharedPreferences(ids);
		 List<Preferences> ret =  preferencesServices.fetchSharedPreferences(name);
		 System.out.println("listPreferences End "+ret.size());
		return ret; 
	}
	
	@PostMapping(value="/acceptSharedPreferences/{userid}/{preferenceName}/{code}")
	public @ResponseBody List<Preferences> acceptSharedPreferences(@RequestBody List<Integer> ids,@PathVariable String userid,
			@PathVariable String preferenceName,@PathVariable String code) {
		 System.out.println(" acceptSharedPreferences List >> "+ids);
		 String  name = userid;
		 List<Preferences> ret =  preferencesServices.fetchSharedPreferences2(name,preferenceName,code);
		 List<Preferences> userPrefs = preferencesServices.fetchPreferences(name);
		 
		 for(Preferences preferences:ret)
		 {
			 try
			 {
				 preferencesServices.savePreferences(preferences);
			 }
			 catch (Exception e) 
			 {
				System.out.println("Delete ID's Here");
			 }
			 
		 }
		 preferencesServices.deleteSharedPreferences(ids);
		 List<Preferences> ret1 =  preferencesServices.fetchSharedPreferences(name);
		 System.out.println("listPreferences End "+ret.size());
		return ret1; 
	}
	
	@PostMapping(value = "/clearPreferences/{userid}")
    public List<Preferences> clearPreferences(@RequestBody List<Integer> clearList,@PathVariable String userid) {
       /* SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        preferencesServices.clearProcessedPreferences(clearList);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();*/
		
		String  name = userid;//servletContext.getAttribute("username").toString();
		preferencesServices.clearProcessedPreferences(clearList);
        List<Preferences> ret =  preferencesServices.fetchPreferences(name);
        return ret; 
    }
	
	
	@PostMapping(value = "/processAllTermRules/{userid}")
    public void processAllTermRules(@RequestBody List<String> termrules,@PathVariable String userid) {
		
       /* String name = SecurityContextHolder.getContext().getAuthentication().getName();
        SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);*/
		String  name = userid;//servletContext.getAttribute("username").toString();
        List<String> allTermRules = termrules.subList(0,termrules.size()-3);
        String entityId = termrules.get(termrules.size()-2);
        String processsId = termrules.get(termrules.size()-1);
        List<Preferences> inProgressList = processUtils.updateInProgress(processsId);
        preferencesServices.updateProcessDetails(inProgressList);
        for(String termRuleId : allTermRules ) {
        	 processingService.processTermRule(termRuleId, 0, entityId, name);
        }
        updateAllProcessDetails(processsId,entityId);
    }
	
	@PostMapping(value = "updateProfile")
    public List<Preferences> updateProfile(@RequestBody Profile preferences) {
       /* String name = SecurityContextHolder.getContext().getAuthentication().getName();
        SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);*/
		String  name = preferences.getUserid();//servletContext.getAttribute("username").toString();
        preferencesServices.updateProfile(preferences);
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       List<Preferences> ret =  preferencesServices.fetchPreferences(name);
       return ret; 
    }
	    
	    private void updateAllProcessDetails(String processsId,String entityId) {
	    	 //LOG.debug("updateAllProcessDetails: " + processsId);
	    	Preferences preferences = new Preferences();
			preferences.setResearchLink("https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&type=&dateb=&owner=exclude&count=40&search_text=&CIK="+entityId);
			//add fdqv link
			String fsqvLink = util.getFsqvLinkProperty();
			preferences.setFsqvLink(fsqvLink + "&cik=" + entityId);
	        //preferences.setFsqvLink("https://md-up-webidap.ix.sec.gov:18443/fsqv-solr/filingSearchSolr?cik="+entityId);
	        preferences.setValidationStatus("Processed");
	        preferences.setResultLink("Processed");
	        preferences.setProcessId(Integer.parseInt(processsId));
	        List<Preferences> list = new ArrayList<Preferences>();
	        list.add(preferences);
	        preferencesServices.updateProcessDetails(list);
	    }
	    
	    
	    @PostMapping(value = "processMultiEntitiesTermRules")
	    public void processMultiEntitiesTermRules(@RequestBody TermRuleProcessing trp) {
	    	System.out.println("Term rules :; "+trp.getEntityId()+trp.getProcessId()+trp.getTermId()+trp.getUserid());
			String  name = trp.getUserid();
			String termRuleId = trp.getTermId();
			 if(trp.getEntityId()!= null && trp.getEntityId()!= "") {
				 List<Preferences> inProgressList = processUtils.updateInProgress(trp.getProcessId());
				 preferencesServices.updateProcessDetails(inProgressList);
				 System.out.println("Enity Name"+trp.getEntityId());
				 processingService.processTermRule(termRuleId, 0, trp.getEntityId(), name); 
				 updateAllProcessDetails(trp.getProcessId(),trp.getEntityId());
			}

 
	    }
	    
	    @CrossOrigin
	    @RequestMapping(value = "/getFeedback", method = RequestMethod.GET)
	    public Object getFeedback() throws IOException {
			
			return preferencesServices.getFeedback();
		}
	    
	    @CrossOrigin
	    @RequestMapping(value = "/getFeedbackByUser", method = RequestMethod.GET)
	    public Object getFeedbackByUser(@RequestParam String username) throws IOException {
			
			return preferencesServices.getFeedbackByUser(username);
		}
	    
		@GetMapping(value = "/profileId")
	    public String getProfileId() {
			String profileId = null;
			try {
				util.getJdbcTemplate();
				profileId = util.getMaxdsProfileId();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	        return profileId;
	    }
	  
}
