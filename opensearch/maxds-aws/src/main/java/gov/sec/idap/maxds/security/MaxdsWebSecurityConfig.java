package gov.sec.idap.maxds.security;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.data.solr.server.support.HttpSolrClientFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


/*@EnableSolrRepositories(
basePackages = "gov.sec.idap.maxds.solr.repository")
@Configuration
@ComponentScan
@Order(SecurityProperties.IGNORED_ORDER)*/
@Order(SecurityProperties.IGNORED_ORDER)
@EnableWebSecurity
public class MaxdsWebSecurityConfig extends WebSecurityConfigurerAdapter{
    
    
	/*
	 * @Autowired Environment env;
	 * 
	 * @Autowired CustomAuthenticationProvider authProvider;
	 * 
	 * 
	 * @Autowired CustomAuthenticationSuccessHandler successHandler;
	 * 
	 * @Autowired CustomLogoutHandler logoutHandler;
	 */
    
   
    @Override
    protected void configure(HttpSecurity http) throws Exception {
       
    	
        
        /*http.authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/#/masdslogin").permitAll()
        .antMatchers("/login").permitAll()
        .antMatchers("/index.html").permitAll()
        .antMatchers("/**"").permitAll()
        .antMatchers("/api/").permitAll()
        .antMatchers("/api/UserRolesList").permitAll()
        .antMatchers("/SECWarning").permitAll()
        .antMatchers("/resources/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .headers().disable()
        .formLogin().disable()
        .csrf().disable()
        .logout().disable();*/
        
    	/*http
    	.headers()
    	.disable()
    	.csrf()
    	.disable()
    	.httpBasic()
    	.and()
    	.authorizeRequests()
    	.antMatchers("/","/index.html","/maxdslogin","/home","/SECWaring","/resources/**")
    	.permitAll()
    	.anyRequest()
    	.authenticated().and()
    	.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());*/
    	
    	/*http.authorizeRequests().antMatchers("/","/index.html","/api/**","/maxdslogin").permitAll().anyRequest().fullyAuthenticated().and().httpBasic().and().csrf().disable();*/
         
        //http.sessionManagement().maximumSessions(1);
        
    	http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
       
    }
    
    
    @Override
	public void configure(WebSecurity web) throws Exception {
	    super.configure(web);

	    web.ignoring().antMatchers("/","/maxdslogin","/**","/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");

	    
	    
	}
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        
        
       // auth.inMemoryAuthentication().withUser("test").password("test").roles("USER","ADMIN");
       // auth.authenticationProvider(authProvider);
    	auth.userDetailsService(userDetailsService());
        
    }
    
    @Bean
    public UserDetailsService userDetailsService() {

    	User.UserBuilder user = User.withDefaultPasswordEncoder();
    	
    	InMemoryUserDetailsManager manger = new InMemoryUserDetailsManager();
    	manger.createUser(user.username("user").password("password").roles("USER").build());
    	manger.createUser(user.username("admin").password("password").roles("USER","AMDIN").build());
    	return manger;
    }
    
 /*   
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }    
    

    @Bean
    public SolrClient solrClient() {
        String solrHost = env.getProperty("spring.data.solr.host","https://sp-us-deraodp01.ix.sec.gov:8983/solr");        
        SolrClient client = new HttpSolrClient.Builder(solrHost).build();
        return client;
    }    
    
    
     @Bean
    public SolrClientFactory solrServerFactory() throws MalformedURLException, IllegalStateException {
        return new HttpSolrClientFactory(solrClient());
    }

    @Bean
    public SolrTemplate solrTemplate() throws Exception {
        SolrTemplate solrTemplate = new SolrTemplate(solrServerFactory());
       
        return solrTemplate;
    }
    
    //File upload 
    @Bean
    public MultipartResolver multipartResolver() {
       CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
       //multipartResolver.setMaxUploadSize(FILES_UPLOAD_LIMIT); 
       //multipartResolver.setMaxUploadSizePerFile(FILE_SIZE_LIMIT); 
       return multipartResolver;
    }    
    */
}
