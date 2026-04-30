package vn.vti.clothing_shop.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import vn.vti.clothing_shop.readmodels.ReadModelRepository;

@Configuration
@EnableJpaRepositories(
        basePackages = "vn.vti.clothing_shop.repositories",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = ReadModelRepository.class
        )
)
@EnableMongoRepositories(basePackageClasses = ReadModelRepository.class)
public class RepositoryConfig {
}
