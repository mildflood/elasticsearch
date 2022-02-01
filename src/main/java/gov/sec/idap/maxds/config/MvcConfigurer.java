package gov.sec.idap.maxds.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration		
public class MvcConfigurer implements WebMvcConfigurer {
	
	@Override  
	public void addViewControllers(ViewControllerRegistry registry) {  
		/*registry.addViewController("/maxds").setViewName("forward:index2.html");
		registry.addViewController("/").setViewName("forward:welcome");
		registry.addViewController("/login").setViewName("login");*/
		//registry.addViewController("/").setViewName("forward:index2.html");
		//registry.addViewController("/login").setViewName("forward:index2.html");
		System.out.println("Entering.......");
	}
	
	@Override  
	public void configurePathMatch(PathMatchConfigurer configurer) {  
		configurer.setUseSuffixPatternMatch(false);  
	}  
}

