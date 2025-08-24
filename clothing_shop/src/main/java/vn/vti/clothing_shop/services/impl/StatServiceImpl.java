package vn.vti.clothing_shop.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class StatServiceImpl {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public Map<String, Long> getStat() {
        HashMap<String, Long> stat = new HashMap<>();
        Long income = orderRepository.sumTotalPriceByDeletedAtIsNullAndPaymentStatus(PaymentStatus.COMPLETED);
        stat.put("income", income);
        stat.put("order", orderRepository.count());
        stat.put("completed", orderRepository.countByDeletedAtIsNullAndPaymentStatus(PaymentStatus.COMPLETED));
        stat.put("user", userRepository.countByDeletedAtIsNullAndRole(UserRole.USER));
        stat.put("product", orderItemRepository.countByDeletedAtIsNullAndOrder_PaymentStatus(PaymentStatus.COMPLETED));
        return stat;
    }

    public Map<Integer, ArrayList<Long>> getMonthlyIncomeForLast5Years() {
        int currentYear = LocalDate.now().getYear();
        List<Integer> fiveRecentYears = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fiveRecentYears.add(currentYear - i);
        }
        Map<Integer, ArrayList<Long>> monthlyIncome = new HashMap<>();
        for (Integer year : fiveRecentYears) {
            ArrayList<Long> monthlyIncomeOfYear = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                Long income = orderRepository.sumTotalPriceByMonthAndYear(i, year);
                monthlyIncomeOfYear.add(income != null ? income : 0L);
            }
            monthlyIncome.put(year, monthlyIncomeOfYear);
        }
        return monthlyIncome;
    }
}
