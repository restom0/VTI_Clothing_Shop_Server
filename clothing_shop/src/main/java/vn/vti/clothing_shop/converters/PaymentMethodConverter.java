package vn.vti.clothing_shop.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.vti.clothing_shop.constants.PaymentMethod;

@Converter
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {
	/** Converts to database column. */
	@Override
	public String convertToDatabaseColumn(PaymentMethod attribute) {
		return attribute == null ? null : attribute.getValue();
	}

	/** Converts to entity attribute. */
	@Override
	public PaymentMethod convertToEntityAttribute(String dbData) {
		return PaymentMethod.fromValue(dbData);
	}
}
