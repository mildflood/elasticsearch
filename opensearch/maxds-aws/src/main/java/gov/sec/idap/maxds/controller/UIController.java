/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.sec.idap.maxds.security.CustomLogoutHandler;


/**
 * Handles requests for the application home page.
 */
@Controller
@CrossOrigin
public class UIController {
        
    @RequestMapping("/logout")
       public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
          if (auth != null){    
             new CustomLogoutHandler().logout(request, response, auth);
             HttpSession session = request.getSession(false);
             session.invalidate();
            // preferencesServices.clearProcessedPreferences((String)auth.getPrincipal());
          }
          //return "forward:/index2.html";
          return "login";
       }     
//       
//    @RequestMapping("/login")
//	public String login(HttpServletRequest request, HttpServletResponse response) {
//		return "forward:/index.html";
//	}   

    @RequestMapping("/loginerror")
	public String loginerror(HttpServletRequest request, HttpServletResponse response) {
		return "loginerror";
	}  
    
    @RequestMapping(value="/csrf-token", method=RequestMethod.GET)
    public @ResponseBody String getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken)request.getAttribute(CsrfToken.class.getName());
        return token.getToken();
    } 
        
}

