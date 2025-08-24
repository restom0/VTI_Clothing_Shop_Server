package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.outs.AuditDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.ForbiddenException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.AuditMapper;
import vn.vti.clothing_shop.repositories.AuditRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.interfaces.AuditService;

import java.util.List;

@Component
@AllArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditMapper auditMapper;
    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    //@Cacheable(value = "Audits")
    public List<AuditDTO> getAllAudits() {
        return auditRepository.findAll()
                .stream()
                .map(auditMapper::entityToDTO)
                .toList();
    }

    //@CacheEvict(value = "Audits", allEntries = true)
    @Transactional
    public void createAudit(AuditDTO auditDTO, Long userId) throws WrapperException {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new ForbiddenException("User not found"));
            auditRepository.save(auditMapper.dtoToEntity(auditDTO, user));
        } catch (ForbiddenException ex) {
            throw new WrapperException(ex);
        }
    }
}


