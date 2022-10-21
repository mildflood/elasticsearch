package gov.sec.idap.maxds.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sec.idap.maxds.util.Util;
import gov.sec.idap.utils.tech.AppEnvironment;
import gov.sec.idap.utils.web.security.IdapUser;
import gov.sec.idap.utils.web.security.SecAdUser;

@CrossOrigin
@Controller
public class MaxdsAuthentication implements AuthenticationProvider {
	@Autowired private JdbcTemplate jtSql;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired Environment env;
    private Util util = new Util();
    
	@RequestMapping("/maxdsLogin")	
	public boolean maxdsLogin(@RequestBody User user) {
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("logging");
		return true;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		SecAdUser secAdUser = authenticateUser(username, password);

		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		// grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
		List<String> listRoles = getAuthorizedRoles(secAdUser.getUserid());
		listRoles.forEach(role -> grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + role.trim())));
		IdapUser appUser = new IdapUser(secAdUser.getUserid(), password, true, true, true, true, grantedAuths, secAdUser.getEmail(),
				secAdUser.getFullUserName());
		Authentication auth = new UsernamePasswordAuthenticationToken(appUser, password, grantedAuths);
		return auth;
	}

	private List<String> getAuthorizedRoles(String userid) {
		String queryUserRoles = "select idapRoleId from [ops1].[UserRole] where idapUserId = ? and profileId = ?";
		List<String> listRoles = jtSql.query(queryUserRoles, new Object[]{userid, AppEnvironment.getInstance().getActiveProfile()},
				(rs, rowNum) -> rs.getString(1).toUpperCase().trim());
		listRoles.add("USER");
		return listRoles;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	public SecAdUser authenticateUser(String username, String password) {
		DirContext context = null;
		NamingEnumeration<SearchResult> searchResults = null;
		try {
			Hashtable<String, String> environment = new Hashtable<String, String>();
			environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			environment.put(Context.PROVIDER_URL, "ldap://172.25.196.112:389");
			environment.put(Context.SECURITY_AUTHENTICATION, "simple");
			environment.put(Context.SECURITY_PRINCIPAL, "AD\\" + username);
			environment.put(Context.SECURITY_CREDENTIALS, password);

			context = new InitialDirContext(environment);

			String searchBase = "DC=AD,DC=SEC,DC=GOV";
			String FILTER = "(&(objectClass=user)(objectCategory=person)((sAMAccountName=" + username + ")))";
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchResults = context.search(searchBase, FILTER, ctls);
			SearchResult searchResult = searchResults.next();
			Attributes attrs = searchResult.getAttributes();
			String fullUserName = "";
			if(username.equalsIgnoreCase("IDAPDEV.service"))
			{
				fullUserName = "IDAPDEV Service";
			}
			else
			{
				fullUserName = attrs.get("givenname").toString().trim().replace("givenName:", "") + " "
					+ attrs.get("sn").toString().trim().replace("sn:", "");
			}
			log.debug("Authenticated: " + username);
			context.close();
			log.debug(username + " LDAP authentication succeed. ");
			return new SecAdUser(username, password, fullUserName);
		} catch (Exception e) {
			log.debug(username + "authentication failed. " + e.getMessage());
		} finally {
			if (null != searchResults) {
				try {
					searchResults.close();
					context.close();
				} catch (NamingException e) {
					log.debug(username + "authentication failed " +e.getMessage());
				}
			}
		}

		throw new BadCredentialsException("Authentication with Active Directory failed. Please re-enter credentials");
	}
	
	public SecAdUser isExistUserIdByComputerName(String idapUser, String idappwd, String computerName) {
    	SecAdUser secUser;
        Hashtable<String, String> environment = new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://172.25.196.112:389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.REFERRAL, "follow");

        String user = "AD\\" + idapUser;
        environment.put(Context.SECURITY_PRINCIPAL, user);

        environment.put(Context.SECURITY_CREDENTIALS, idappwd);

        DirContext context = null;
        NamingEnumeration<SearchResult> searchResults = null;
        try {
            context = new InitialDirContext(environment);

            String searchBase = "DC=AD,DC=SEC,DC=GOV";
            String userName ="";
            String fullUserName="";
            String userId = "";
            String lastName = "";
            String FILTER =  "(&(objectClass=user)(objectcategory=computer)(name=*" + computerName + "*))";
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchResults = context.search(searchBase, FILTER, ctls);
            SearchResult searchResult = null;
            if(searchResults.hasMore()) {
	            searchResult = searchResults.next();
	            userName = (String) searchResult.getAttributes().get("managedBy").get();
	            userId = (String) searchResult.getAttributes().get("samaccountname").get();
	            userName = userName.substring(userName.indexOf("=")+1);
	            fullUserName = userName.substring(0,userName.indexOf("="));
	            userName = userName.substring(0,userName.indexOf("="));
	            fullUserName = fullUserName.replace("\\,", "");
	            fullUserName = fullUserName.substring(0 , fullUserName.indexOf(","));
	            userId = fullUserName.substring(0, fullUserName.indexOf(" ") +2).toLowerCase().replaceAll("\\s+", "");
	            lastName = fullUserName.substring(0, fullUserName.indexOf(" "));
	            userName = userName.substring(userName.indexOf("\\,") + 2,  userName.length());
	            userName = userName.substring(0 , userName.indexOf(","));
	            
	            FILTER = "(&(objectClass=user)(objectCategory=person)((cn=" +  lastName +","+ userName + ")))";
	            searchResults = context.search(searchBase, FILTER, ctls);
	            Attributes attrs = searchResult.getAttributes();
	            searchResult = searchResults.next();
	            userId = (String) searchResult.getAttributes().get("sAMAccountName").get();
	           
	            context.close();
//	            }
	            System.out.println("userName :  " + userName);
	            secUser = new SecAdUser(userId.toLowerCase(), userName.trim(), fullUserName);
	            
	            String userAuth = env.getProperty("user.auth", "None");
	            List<String> userList = null;
	            if (userAuth.contains("None")) {

	                String users = env.getProperty("user.list", "srinivasans,admin,huangy,sec");
	                userList = Arrays.asList(users.split(","));
	            } else {
	                String profileId = env.getProperty("maxds.profileId", "default");

	                userList = new ArrayList<String>();
	                try {
	                    userList = util.getResultSet(false);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }

	            }
	            // }
	            //String users = env.getProperty("user.list", "srinivasans,admin,huangy,sec");
	            //List<String> userList = Arrays.asList(users.split(","));

	            if (userList != null && !userList.isEmpty() && userList.contains(userId.toLowerCase())) {
	            	log.info(">>>>>>>>>>>>>>>>>>> User Registered and logging in to MAXDS with userID " + userId.toLowerCase() + " host " + computerName+ " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	               
	            } else {
	            	log.info(">>>>>>>>>>>>>>>>>>> User not in MAXDS with User Name"+ userId+ " <<<<<<<<<<<<<<<<<<<");
	                throw new BadCredentialsException("User name not available in the list of valid MAXDS users. Please contact support.");
	            }
	            
            } else {
            	return null;
            }
            
        } catch (Exception e) {
        	return null;
        } finally {
            if (null != searchResults) {
                try {
                    searchResults.close();
                    context.close();
                } catch (NamingException e) {
                }
            }
        }
        return secUser;
    }
}
