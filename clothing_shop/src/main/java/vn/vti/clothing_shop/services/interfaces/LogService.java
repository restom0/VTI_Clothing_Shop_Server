package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.LogCreateDTO;
import vn.vti.clothing_shop.dtos.outs.LogDTO;

import java.util.List;

public interface LogService {
    List<LogDTO> getAllLogs();
    void createLog(LogCreateDTO logCreateDTO);

}
