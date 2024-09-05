package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.ProductCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;

import java.util.List;

@Component
public interface ProductService {
    public List<ProductDTO> getAllProducts();
    public Boolean addProduct(ProductCreateDTO productCreateDTO);
    public Boolean deleteProduct(Long id);
    public Boolean updateProduct(ProductUpdateDTO productUpdateDTO);
    public ProductDTO getProductById(Long id);
}
