package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.vti.clothing_shop.dtos.outs.AuditDTO;
import vn.vti.clothing_shop.entities.Audit;
import vn.vti.clothing_shop.entities.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = { DateMapper.class, UserMapper.class })
public interface AuditMapper {
	@Mapping(target = "column", source = "filterColumn")
	AuditDTO entityToDTO(Audit audit);

	List<AuditDTO> listEntityToDTO(List<Audit> audits);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", source = "user")
	@Mapping(target = "filterColumn", source = "auditDTO.column")
	@Mapping(target = "createdAt", ignore = true)
	Audit dtoToEntity(AuditDTO auditDTO, User user);

}
