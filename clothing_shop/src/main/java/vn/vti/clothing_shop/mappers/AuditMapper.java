package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.vti.clothing_shop.dtos.outs.AuditDTO;
import vn.vti.clothing_shop.entities.Audit;
import vn.vti.clothing_shop.entities.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditMapper {
    AuditDTO entityToDTO(Audit audit);

    List<AuditDTO> listEntityToDTO(List<Audit> audits);

    @Mapping(target = "auditDTO.user", ignore = true)
    Audit dtoToEntity(AuditDTO auditDTO, User user);

}
