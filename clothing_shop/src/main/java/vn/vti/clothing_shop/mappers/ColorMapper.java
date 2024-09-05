package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.ColorCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ColorUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.dtos.outs.ColorDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Color;

import java.util.List;

@Component
public class ColorMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public ColorMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ColorDTO EntityToDTO(Color color) {
        ColorDTO colorDTO = modelMapper.map(color, ColorDTO.class);
        colorDTO.setCategory_id(modelMapper.map(color.getCategory_id(), CategoryDTO.class));
        return colorDTO;
    }

    public List<ColorDTO> EntityToDTO(List<Color> colors) {
        return colors.stream().map(this::EntityToDTO).toList();
    }

    public Color toEntity(ColorDTO colorDTO) {
        Color color = modelMapper.map(colorDTO, Color.class);
        color.setCategory_id(modelMapper.map(colorDTO.getCategory_id(), Category.class));
        return color;
    }

    public Color ColorCreateDTOToEntity(ColorCreateDTO colorCreateDTO, Category category) {
        Color color = modelMapper.map(colorCreateDTO, Color.class);
        color.setColor_code(colorCreateDTO.getColor_code().toUpperCase());
        color.setCategory_id(category);
        return color;
    }

    public Color ColorUpdateDTOToEntity(ColorUpdateDTO colorUpdateDTO, Color color) {
        color.setColor_code(colorUpdateDTO.getColor_code().toUpperCase());
        color.setColor_name(colorUpdateDTO.getColor_name());
        return color;
    }
}
