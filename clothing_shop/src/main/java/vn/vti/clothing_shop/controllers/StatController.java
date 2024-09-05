package vn.vti.clothing_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.StatServiceImplementation;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stat")
public class StatController {
    private final StatServiceImplementation statServiceImplementation;

    @Autowired
    public StatController(StatServiceImplementation statServiceImplementation) {
        this.statServiceImplementation = statServiceImplementation;
    }
    @GetMapping("/analysis")
    public ResponseEntity<?> getStatisticalAnalysis() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("generalStats", statServiceImplementation.getStat());
            response.put("monthlyIncome", statServiceImplementation.getMonthlyIncomeForLast5Years());
            return ResponseHandler.responseBuilder(200, "Lấy dữ liệu thống kê thành công", response, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
