package vn.vti.clothing_shop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.AuditService;

@AllArgsConstructor
@RestController
@RequestMapping("/audit")
public class AuditController {
    private final AuditService auditService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllAudits() {
        return ResponseHandler.successBuilder(HttpStatus.OK, auditService.getAllAudits());
    }
}
