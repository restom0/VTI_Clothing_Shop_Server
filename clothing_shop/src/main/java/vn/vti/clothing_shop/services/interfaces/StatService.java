package vn.vti.clothing_shop.services.interfaces;

import java.util.ArrayList;
import java.util.Map;

public interface StatService {
	/** Gets stat. */
	Map<String, Long> getStat();

	/** Gets monthly income for last 5 years. */
	Map<Integer, ArrayList<Long>> getMonthlyIncomeForLast5Years();
}
