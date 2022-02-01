package gov.sec.idap.maxds.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "gov.sec.idap.maxds.elasticsearch.repository")
@ComponentScan(basePackages = {"gov.sec.idap.maxds.elasticsearch"})
public class EsConfig extends AbstractElasticsearchConfiguration {

	@Value("${elasticsearch.url}")
    public String elasticsearchUrl;
	
	@Value("${elasticsearch.username}")
    public String elasticsearchUserName;
	
	@Value("${elasticsearch.password}")
    public String elasticsearchPassword;

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl).usingSsl().withBasicAuth(elasticsearchUserName, elasticsearchPassword)
                .build();

        return RestClients.create(config).rest();
    }
    
//    @Bean
//    public ElasticsearchRestTemplate elasticsearchTemplate() {
//        return new ElasticsearchRestTemplate(elasticsearchClient());
//    }

//    @SuppressWarnings("deprecation")
//	@Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchTemplate((Client) elasticsearchClient());
//    }
    
}
