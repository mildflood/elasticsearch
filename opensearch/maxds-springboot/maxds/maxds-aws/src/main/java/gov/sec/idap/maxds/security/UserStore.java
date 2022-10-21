/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.security;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.User;

public class UserStore {

	private static UserStore instance = null;
	private Map<String, HttpSession> mapUserName2User = new HashMap<>();
	


	protected UserStore() {
		
	}

	public static UserStore getInstance() {
		if (instance == null) {
			instance = new UserStore();
		}
		return instance;
	}

	

	public HttpSession getUserSession(String userName) {
		return mapUserName2User.get(userName);
	}

	public void addUser(String userName, HttpSession session) throws Exception {
		mapUserName2User.put(userName, session);
	}

	public void removeUser(String userName) {
		
		mapUserName2User.remove(userName);
	}

	
}
