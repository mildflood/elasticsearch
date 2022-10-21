package gov.sec.idap.maxds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
public class MainJspController {
	@Autowired Environment env;
	private String json="";
//	@RequestMapping("/welcome")
//	public String welcome(Model model, Authentication auth) {
//
//		return "forward:/index.html";
//	}
	
	@RequestMapping({"/","/login","/welcome"})
	public String anyRoute() {
		System.out.println("request >>");
		return "forward:/index.html";
	}
}
