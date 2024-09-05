package vn.vti.clothing_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.LogServiceImplementation;

@RestController
@RequestMapping("/log")
public class LogController {
    private final LogServiceImplementation logServiceImplementation;

    @Autowired
    public LogController(LogServiceImplementation logServiceImplementation) {
        this.logServiceImplementation = logServiceImplementation;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllLogs(){
        try{
            return ResponseHandler.responseBuilder(200,"Lấy danh sách log thành công",logServiceImplementation.getAllLogs(), HttpStatus.OK);
        }
        catch (Exception e){
return ResponseHandler.exceptionBuilder(e);
        }
    }
}
