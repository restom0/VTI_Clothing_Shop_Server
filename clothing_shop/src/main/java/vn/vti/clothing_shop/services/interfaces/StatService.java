package vn.vti.clothing_shop.services.interfaces;

import java.util.ArrayList;
import java.util.Map;

public interface StatService {
    Map<String, Long> getStat();

    Map<Integer, ArrayList<Long>> getMonthlyIncomeForLast5Years();
}
