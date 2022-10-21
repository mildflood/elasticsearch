package gov.sec.idap.maxds.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "gov.sec.idap.maxds.elasticsearch.repository")
@ComponentScan(basePackages = {"gov.sec.idap.maxds.elasticsearch"})
public class EsConfig extends AbstractElasticsearchConfiguration {

	// OpenSearch
	@Value("${opensearch.domain.endpoint}")
	public String osUrl;

	@Value("${opensearch.username}")
	public String osUserName;

	@Value("${opensearch.password}")
	public String osPassword;

	@Bean
	@Override
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration config = ClientConfiguration.builder().connectedTo(osUrl + ":443").usingSsl()
				.withBasicAuth(osUserName, osPassword).withSocketTimeout(300000).build();
		
		return RestClients.create(config).rest();
	}
	
//    @Bean
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        final ClientConfiguration config = ClientConfiguration.builder()
//                .connectedTo(elasticsearchUrl).usingSsl().withBasicAuth(elasticsearchUserName, elasticsearchPassword)
//                .build();
//
//        return RestClients.create(config).rest();
//    }
    
}
