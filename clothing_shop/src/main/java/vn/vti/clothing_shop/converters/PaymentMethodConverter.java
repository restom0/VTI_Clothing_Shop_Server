package vn.vti.clothing_shop.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.vti.clothing_shop.constants.PaymentMethod;

@Converter
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {
	@Override
	public String convertToDatabaseColumn(PaymentMethod attribute) {
		return attribute == null ? null : attribute.getValue();
	}

	@Override
	public PaymentMethod convertToEntityAttribute(String dbData) {
		return PaymentMethod.fromValue(dbData);
	}
}
