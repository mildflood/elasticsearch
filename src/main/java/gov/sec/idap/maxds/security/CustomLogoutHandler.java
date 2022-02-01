/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import gov.sec.idap.maxds.security.UserStore;

@CrossOrigin
@Component
public class CustomLogoutHandler implements LogoutHandler {
    
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        
      
       String name = (String)authentication.getPrincipal();
     
       if( UserStore.getInstance().getUserSession(name) != null )
        {
                      
          
           UserStore.getInstance().removeUser(name);
            
            
        }
       
       
    }

   
}


