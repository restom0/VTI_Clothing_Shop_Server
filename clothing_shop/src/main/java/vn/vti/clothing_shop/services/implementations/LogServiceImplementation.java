package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.LogCreateDTO;
import vn.vti.clothing_shop.dtos.outs.LogDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.ForbiddenException;
import vn.vti.clothing_shop.mappers.LogMapper;
import vn.vti.clothing_shop.repositories.LogRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.interfaces.LogService;

import java.util.List;

@Component
public class LogServiceImplementation implements LogService {
    private final LogMapper logMapper;
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    @Autowired
    public LogServiceImplementation(LogMapper logMapper, LogRepository logRepository, UserRepository userRepository) {
        this.logMapper = logMapper;
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }
    //@Cacheable(value = "logs")
    public List<LogDTO> getAllLogs(){
        return logMapper.ListEntityToDTO(logRepository.findAll());
    };

    //@CacheEvict(value = "logs", allEntries = true)
    @Transactional
    public void createLog(LogCreateDTO logCreateDTO){
        User user = userRepository.findById(logCreateDTO.getUser_id()).orElseThrow(()->new ForbiddenException("User not found"));
        logRepository.save(logMapper.CreateDTOToEntity(logCreateDTO,user));
    };
}


