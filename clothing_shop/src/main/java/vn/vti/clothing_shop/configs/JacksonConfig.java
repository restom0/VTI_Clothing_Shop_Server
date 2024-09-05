package vn.vti.clothing_shop.configs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.vti.clothing_shop.dtos.outs.*;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        mapper.activateDefaultTyping(
//                BasicPolymorphicTypeValidator.builder().build(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
        //mapper.registerSubtypes(
//                new NamedType(CategoryDTO.class, "CategoryDTO"),
//                new NamedType(MaterialDTO.class, "MaterialDTO"),
//                new NamedType(ProductDTO.class, "ProductDTO"),
//                new NamedType(ImportedProductDTO.class, "ImportedProductDTO"),
//                new NamedType(ColorDTO.class, "ColorDTO"),
//                new NamedType(SizeDTO.class, "SizeDTO"),
//                new NamedType(BrandDTO.class, "BrandDTO"),
//                new NamedType(LogDTO.class, "LogDTO"),
//                new NamedType(UserDTO.class, "UserDTO")
//        );
//        mapper.registerSubtypes(
//                new NamedType(CategoryDTO.class, "CategoryDTO"),
//                new NamedType(MaterialDTO.class, "MaterialDTO"),
//                new NamedType(ProductDTO.class, "ProductDTO"),
//                new NamedType(ImportedProductDTO.class, "ImportedProductDTO"),
//                new NamedType(ColorDTO.class, "ColorDTO"),
//                new NamedType(SizeDTO.class, "SizeDTO"),
//                new NamedType(BrandDTO.class, "BrandDTO"),
//                new NamedType(LogDTO.class, "LogDTO")
//        );
        return mapper;
    }
}