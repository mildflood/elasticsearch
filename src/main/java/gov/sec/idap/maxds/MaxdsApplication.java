package gov.sec.idap.maxds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

//import gov.sec.idap.utils.tech.LangUtil;

@EnableAsync
@EnableScheduling
@SpringBootApplication
//@EnableSolrRepositories(basePackages = "gov.sec.idap.maxds.solr.repository")
//@EnableElasticsearchRepositories(basePackages = "gov.sec.idap.maxds.elasticsearch.repository")
public class MaxdsApplication implements CommandLineRunner {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(MaxdsApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		String banner = LangUtil.resourceToString("classpath:banner.txt");
//		log.info("MAXDS is starting" + System.lineSeparator() + banner);
//	}
	@Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }    
    
//	@Autowired
//    Environment env;
//    @Bean
//    public SolrClient solrClient() {
//        String solrHost = env.getProperty("spring.data.solr.host","https://sp-us-deraodp01.ix.sec.gov:8983/solr");        
//        SolrClient client = new HttpSolrClient.Builder(solrHost).build();
//        return client;
//    }    
//     @Bean
//    public SolrClientFactory solrServerFactory() throws MalformedURLException, IllegalStateException {
//        return new HttpSolrClientFactory(solrClient());
//    }
//
//    @Bean
//    public SolrTemplate solrTemplate() throws Exception {
//        SolrTemplate solrTemplate = new SolrTemplate(solrServerFactory());
//       
//        return solrTemplate;
//    }
    
    //File upload 
//    @Bean
//    public MultipartResolver multipartResolver() {
//       CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//       //multipartResolver.setMaxUploadSize(FILES_UPLOAD_LIMIT); 
//       //multipartResolver.setMaxUploadSizePerFile(FILE_SIZE_LIMIT); 
//       return multipartResolver;
//    }

@Override
public void run(String... args) throws Exception {
	// TODO Auto-generated method stub
	
} 

}
