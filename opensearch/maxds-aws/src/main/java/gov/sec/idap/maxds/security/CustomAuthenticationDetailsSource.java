/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.security;


import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


public class CustomAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    @Override
  public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
    return new CustomAuthenticationDetails(context);
  }

  @SuppressWarnings("serial")
 public class CustomAuthenticationDetails extends WebAuthenticationDetails {

    private Boolean invalidate= false;

    public CustomAuthenticationDetails(HttpServletRequest request) {
      super(request);
      
      String s = request.getParameter("checkbox1") != null ? request.getParameter("checkbox1") : "";
      invalidate = s.equals("1");
    }

   
    
    public Boolean invalidateSession()
    {
        return invalidate;
    }
  }
    
}
