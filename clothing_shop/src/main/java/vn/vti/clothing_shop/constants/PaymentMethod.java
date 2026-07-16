package vn.vti.clothing_shop.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
	COD("COD"),
	E_BANKING("EBanking"),
	MOMO("MOMO"),
	PAYOS("PAYOS"),
	STRIPE("STRIPE"),
	ZALO_PAY("ZALO_PAY");

	private final String value;

	PaymentMethod(String value) {
		this.value = value;
	}

	@JsonCreator
	public static PaymentMethod fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (PaymentMethod paymentMethod : values()) {
			if (paymentMethod.name().equals(value) || paymentMethod.value.equals(value)) {
				return paymentMethod;
			}
		}
		throw new IllegalArgumentException("Unknown payment method: " + value);
	}

	@JsonValue
	public String getValue() {
		return value;
	}
}
