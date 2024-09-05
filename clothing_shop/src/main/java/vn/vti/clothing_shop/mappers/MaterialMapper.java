package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.MaterialCreateDTO;
import vn.vti.clothing_shop.dtos.ins.MaterialUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.dtos.outs.MaterialDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Material;

import java.util.List;

@Component
public class MaterialMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public MaterialMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MaterialDTO EntityToDTO(Material material) {
        MaterialDTO materialDTO = modelMapper.map(material, MaterialDTO.class);
        materialDTO.setCategory_id(modelMapper.map(material.getCategory_id(), CategoryDTO.class));
        return materialDTO;
    }

    public List<MaterialDTO> EntityToDTO(List<Material> materials) {
        return materials.stream().map(this::EntityToDTO).toList();
    }

    public Material MaterialCreateDTOToEntity(MaterialCreateDTO materialCreateDTO, Category category) {
        Material material = modelMapper.map(materialCreateDTO, Material.class);
        material.setName(materialCreateDTO.getMaterial());
        material.setCategory_id(category);
        return material;
    }

    public Material MaterialUpdateDTOToEntity(MaterialUpdateDTO materialUpdateDTO, Material material) {
        material.setName(materialUpdateDTO.getMaterial());
        return material;
    }
}
