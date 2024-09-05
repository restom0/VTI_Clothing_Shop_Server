package vn.vti.clothing_shop.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

//    @javax.annotation.PostConstruct
//    public void logDataSourceUrl() {
//        logger.info("Configured DataSource URL: {}", dataSourceUrl);
//    }
}
