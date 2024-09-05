package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.LogCreateDTO;
import vn.vti.clothing_shop.dtos.outs.LogDTO;
import vn.vti.clothing_shop.entities.Log;
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

   private LogDTO EntityToDTO(Log log) {
        LogDTO logDTO = modelMapper.map(log, LogDTO.class);
        logDTO.setUser_id(userMapper.EntityToDTO(log.getUser_id()));
        return logDTO;
    }
    public List<LogDTO> ListEntityToDTO(List<Log> logs) {
        return logs.stream().map(this::EntityToDTO).collect(Collectors.toList());
    }
    public Log CreateDTOToEntity(LogCreateDTO logCreateDTO, User user) {
        Log log = modelMapper.map(logCreateDTO, Log.class);
        log.setUser_id(user);
        return log;
    }
}
