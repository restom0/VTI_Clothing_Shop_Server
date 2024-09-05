package vn.vti.clothing_shop.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import vn.vti.clothing_shop.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParameterUtils {
    public boolean checkNullorNanParameter(ArrayList<?>parameter){
        return true;
    }
    public static ResponseEntity<?> showBindingResult(BindingResult bindingResult){
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        throw new BadRequestException(errors.toString());
    }
}
