/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.security;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import gov.sec.idap.maxds.security.CustomAuthenticationDetailsSource.CustomAuthenticationDetails;
import gov.sec.idap.maxds.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    Environment env;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private Util util = new Util();
    

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        String userAuth = env.getProperty("user.auth", "None");

        if (!userAuth.contains("None")) {

            try {
                if (!isValidInLDAP(name, password)) {
                    throw new BadCredentialsException("Authentication with Active Directory failed. Please re-enter credentials");
                }
            } catch (Exception exp) {
                throw new BadCredentialsException("Authentication with Active Directory failed. Please re-enter credentials");
            }
        }

        if (UserStore.getInstance().getUserSession(name) != null) {
            Object obj = authentication.getDetails();
            if (obj instanceof CustomAuthenticationDetails) {

                CustomAuthenticationDetails input = (CustomAuthenticationDetails) obj;

                if (input.invalidateSession()) {

                    UserStore.getInstance().getUserSession(name).invalidate();
                } else {

                    String msg = String.format("Username: %s. You are already logged in. Current datetime %s", name, new Date());

                    throw new SessionAuthenticationException(msg);
                }

            } else {
                throw new SessionAuthenticationException("Login failed, please try again.");
            }

        }
        List<String> userList = null;
        if (userAuth.contains("None")) {

            String users = env.getProperty("user.list", "srinivasans,admin,huangy,sec");
            userList = Arrays.asList(users.split(","));
        } else {
            String profileId = env.getProperty("msaxds.profileId", "default");

            userList = new ArrayList<String>();
            try {
                userList = util.getResultSet(false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (userList != null && !userList.isEmpty() && userList.contains(name)) {

            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);

            return auth;
        } else {
            throw new BadCredentialsException("User name not available in the list of valid MAXDS users. Please contact support.");
        }
    }

    public Boolean isValidInLDAP(String username, String password) {

        Hashtable<String, String> environment = new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://172.25.196.112:389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");

        String user = "AD\\" + username;
        environment.put(Context.SECURITY_PRINCIPAL, user);

        environment.put(Context.SECURITY_CREDENTIALS, password);

        DirContext context = null;
        NamingEnumeration<SearchResult> searchResults = null;
        try {
            context = new InitialDirContext(environment);

            String searchBase = "DC=AD,DC=SEC,DC=GOV";
            String FILTER = "(&(objectClass=user)(objectCategory=person)((sAMAccountName=" + username + ")))";
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchResults = context.search(searchBase, FILTER, ctls);
            SearchResult searchResult = searchResults.next();
           
            context.close();
            log.debug(username + " LDAP authentication succeed. ");
            return searchResult != null;
        } catch (AuthenticationException e) {
        	log.debug("LDAP Authentication failed:" + e.getMessage());
        } catch (NamingException e) {
        	log.debug("LDAP Authentication failed:" + e.getMessage());
        } catch (Exception e) {
        	log.debug("LDAP Authentication failed:" + e.getMessage());
        } finally {
            if (null != searchResults) {
                try {
                    searchResults.close();
                    context.close();
                } catch (NamingException e) {
                	log.debug("LDAP Authentication failed:" + e.getMessage());
                }
            }
        }

        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
