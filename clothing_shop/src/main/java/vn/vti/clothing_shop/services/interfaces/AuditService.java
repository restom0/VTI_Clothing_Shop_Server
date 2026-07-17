package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.outs.AuditDTO;
import vn.vti.clothing_shop.entities.Audit;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface AuditService {
	/** Gets all audits. */
	List<Audit> getAllAudits();

	/** Creates audit. */
	void createAudit(AuditDTO auditDTO, Long userId) throws WrapperException;
}
