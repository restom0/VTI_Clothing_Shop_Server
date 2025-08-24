package vn.vti.clothing_shop.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.StatService;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/stat")
public class StatController {
    private final StatService statService;

    @GetMapping("/analysis")
    public ResponseEntity<BaseMessageResponse> getStatisticalAnalysis() {
        Map<String, Object> response = new HashMap<>();
        response.put("generalStats", statService.getStat());
        return ResponseHandler.successBuilder(HttpStatus.OK, "Lấy dữ liệu thống kê thành công", response);
    }
}
