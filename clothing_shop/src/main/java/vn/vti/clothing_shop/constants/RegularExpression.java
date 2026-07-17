package vn.vti.clothing_shop.constants;

import java.util.regex.Pattern;

public class RegularExpression {
	public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
	public static final Pattern PHONE_NUMBER = Pattern.compile("\\+?[0-9 .()-]+");
	public static final Pattern COLOR = Pattern.compile("^#[a-fA-F0-9]{6}$");

	/** Creates RegularExpression instance. */
	private RegularExpression() {
		throw new IllegalStateException("Utility class");
	}
}
