package vn.vti.clothing_shop.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.InputSaleFilter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InputSale extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private InputSaleFilter filter;

	private Long filterId;

	@Column(nullable = false, precision = 3, columnDefinition = "CHECK sale_percentage >= 0")
	private Integer salePercentage;

	@Column(nullable = false, columnDefinition = "SMALLINT CHECK (discount >= 0 AND discount <= 100)")
	private Byte discount;

	@Column(nullable = false)
	private LocalDate startDate;

	private LocalDate endDate;

}