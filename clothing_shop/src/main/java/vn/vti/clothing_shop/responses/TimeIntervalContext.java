package vn.vti.clothing_shop.responses;

import java.util.Locale;

public final class TimeIntervalContext {
	private static final ThreadLocal<Locale> LOCALE = new ThreadLocal<>();

	private TimeIntervalContext() {
	}

	public static void enable(Locale locale) {
		LOCALE.set(locale);
	}

	public static boolean isEnabled() {
		return LOCALE.get() != null;
	}

	public static Locale locale() {
		Locale locale = LOCALE.get();
		return locale == null ? Locale.ENGLISH : locale;
	}

	public static void clear() {
		LOCALE.remove();
	}
}
