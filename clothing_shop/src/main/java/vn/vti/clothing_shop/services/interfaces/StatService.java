package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public interface StatService {
    public HashMap<String,Long> getStat();
}
