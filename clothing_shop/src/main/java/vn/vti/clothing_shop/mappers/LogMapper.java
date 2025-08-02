package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.LogCreateDTO;
import vn.vti.clothing_shop.dtos.outs.LogDTO;
import vn.vti.clothing_shop.entities.Audit;
import vn.vti.clothing_shop.entities.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LogMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    @Autowired
    public LogMapper(ModelMapper modelMapper, UserMapper userMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }

   private LogDTO EntityToDTO(Audit audit) {
        LogDTO logDTO = modelMapper.map(audit, LogDTO.class);
        logDTO.setUser_id(userMapper.EntityToDTO(audit.getUser_id()));
        return logDTO;
    }
    public List<LogDTO> ListEntityToDTO(List<Audit> audits) {
        return audits.stream().map(this::EntityToDTO).collect(Collectors.toList());
    }
    public Audit CreateDTOToEntity(LogCreateDTO logCreateDTO, User user) {
        Audit audit = modelMapper.map(logCreateDTO, Audit.class);
        audit.setUser_id(user);
        return audit;
    }
}
