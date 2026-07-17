package vn.vti.clothing_shop.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.vti.clothing_shop.utils.TimeUtils;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeZoneConfig {

	/** Handles application clock. */
	@Bean
	public Clock applicationClock(@Value("${application.time-zone:UTC}") String timeZone) {
		ZoneId zoneId = ZoneId.of(timeZone);
		TimeUtils.setZoneId(zoneId);
		return Clock.system(zoneId);
	}
}
