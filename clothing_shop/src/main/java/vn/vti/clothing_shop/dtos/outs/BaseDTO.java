package vn.vti.clothing_shop.dtos.outs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "@class"
//)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = ImportedProductDTO.class, name = "ImportedProductDTO"),
//        @JsonSubTypes.Type(value = ColorDTO.class, name = "ColorDTO"),
//        @JsonSubTypes.Type(value = CategoryDTO.class, name = "CategoryDTO"),
//        @JsonSubTypes.Type(value = BrandDTO.class, name = "BrandDTO"),
//        @JsonSubTypes.Type(value = MaterialDTO.class, name = "MaterialDTO"),
//        @JsonSubTypes.Type(value = ProductDTO.class, name = "ProductDTO"),
//        @JsonSubTypes.Type(value = SizeDTO.class, name = "SizeDTO"),
//        @JsonSubTypes.Type(value = UserDTO.class, name = "UserDTO"),
//})
@Getter
@Setter
@AllArgsConstructor
public abstract class BaseDTO {
}