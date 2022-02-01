package gov.sec.idap.maxds.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = {"gov.sec.idap.maxds"})
@ComponentScan("gov.sec.idap.utils")
@ComponentScan("gov.sec.idap.maxds.elasticsearch")
public class AppConfig {

}