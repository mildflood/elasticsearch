/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.security;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@Component
public class SecurityListener implements HttpSessionListener {

	public SecurityListener() {
	}

	public void sessionCreated(HttpSessionEvent arg0) {
            
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
            
            
		String currentUser = (String) arg0.getSession().getAttribute("username");
		if (currentUser != null) {
                    
			UserStore.getInstance().removeUser(currentUser);
			
		}
	}
}