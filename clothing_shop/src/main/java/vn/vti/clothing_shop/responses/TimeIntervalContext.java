package vn.vti.clothing_shop.responses;

import java.util.Locale;

public final class TimeIntervalContext {
	private static final ThreadLocal<Locale> LOCALE = new ThreadLocal<>();

	/** Creates TimeIntervalContext instance. */
	private TimeIntervalContext() {
	}

	/** Handles enable. */
	public static void enable(Locale locale) {
		LOCALE.set(locale);
	}

	/** Checks whether enabled. */
	public static boolean isEnabled() {
		return LOCALE.get() != null;
	}

	/** Handles locale. */
	public static Locale locale() {
		Locale locale = LOCALE.get();
		return locale == null ? Locale.ENGLISH : locale;
	}

	/** Handles clear. */
	public static void clear() {
		LOCALE.remove();
	}
}
