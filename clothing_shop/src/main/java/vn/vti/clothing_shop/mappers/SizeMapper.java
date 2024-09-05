package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.SizeCreateDTO;
import vn.vti.clothing_shop.dtos.ins.SizeUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.dtos.outs.SizeDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Size;

import java.util.List;

@Component
public class SizeMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public SizeMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SizeDTO EntityToDTO(Size size) {
        SizeDTO sizeDTO = modelMapper.map(size, SizeDTO.class);
        sizeDTO.setCategory_id(modelMapper.map(size.getCategory_id(), CategoryDTO.class));
        return sizeDTO;
    }

    public List<SizeDTO> EntityToDTO(List<Size> sizes) {
        return sizes.stream().map(this::EntityToDTO).toList();
    }

    public Size toEntity(SizeDTO sizeDTO) {
        Size size = modelMapper.map(sizeDTO, Size.class);
        size.setCategory_id(modelMapper.map(sizeDTO.getCategory_id(), Category.class));
        return size;
    }

    public Size SizeCreateDTOToEntity(SizeCreateDTO sizeCreateDTO, Category category) {
        Size size = modelMapper.map(sizeCreateDTO, Size.class);
        size.setCategory_id(category);
        return size;
    }

    public Size SizeUpdateDTOToEntity(SizeUpdateDTO sizeUpdateDTO, Size size) {
        size.setSize(sizeUpdateDTO.getSize());
        size.setHeight(sizeUpdateDTO.getHeight());
        size.setWeight(sizeUpdateDTO.getWeight());
        return size;
    }
}
