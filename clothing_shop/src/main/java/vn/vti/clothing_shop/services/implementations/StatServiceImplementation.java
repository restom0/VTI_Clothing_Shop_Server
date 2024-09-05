package vn.vti.clothing_shop.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.UserRepository;

import java.time.LocalDate;
import java.util.*;

@Component
public class StatServiceImplementation {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public StatServiceImplementation(OrderRepository orderRepository, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public HashMap<String,Long> getStat(){
        HashMap<String,Long> stat = new HashMap<>();
        Long income = orderRepository.sumTotalPrice();
        stat.put("income", income!=null?income:0L);
        stat.put("order", orderRepository.count());
        stat.put("completed",orderRepository.countCompletedOrder());
        stat.put("user", userRepository.count());
        stat.put("product",orderItemRepository.countAllByCompletedOrder());
        return stat;
    };
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
