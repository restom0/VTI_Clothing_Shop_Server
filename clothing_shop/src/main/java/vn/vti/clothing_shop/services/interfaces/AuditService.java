package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.outs.AuditDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface AuditService {
    List<AuditDTO> getAllAudits();

    void createAudit(AuditDTO auditDTO, Long userId) throws WrapperException;
}
