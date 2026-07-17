package vn.vti.clothing_shop.responses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class MessageResolverTest {
	@BeforeEach
	void setUp() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("labels/labels");
		messageSource.setDefaultEncoding("UTF-8");
		new MessageResolver(messageSource);
	}

	@Test
	void resolveIfMessageKeyResolvesStringsAndLeavesOtherValuesUntouched() {
		Object marker = new Object();

		assertThat(MessageResolver.resolveIfMessageKey("{messages.users.notfound}")).isEqualTo("User not found");
		assertThat(MessageResolver.resolveIfMessageKey(marker)).isSameAs(marker);
	}

	@Test
	void resolveReturnsOriginalValueForNullBlankMissingSourceOrUnknownKey() {
		assertThat(MessageResolver.resolve(Locale.ENGLISH, (String) null)).isNull();
		assertThat(MessageResolver.resolve(Locale.ENGLISH, " ")).isEqualTo(" ");
		assertThat(MessageResolver.resolve(Locale.ENGLISH, "messages.unknown")).isEqualTo("messages.unknown");

		AtomicReference<MessageSource> holder = messageSourceHolder();
		MessageSource existingSource = holder.get();
		holder.set(null);
		try {
			assertThat(MessageResolver.resolve(Locale.ENGLISH, "messages.users.notfound"))
					.isEqualTo("messages.users.notfound");
		} finally {
			holder.set(existingSource);
		}
	}

	@SuppressWarnings("unchecked")
	private AtomicReference<MessageSource> messageSourceHolder() {
		return (AtomicReference<MessageSource>) ReflectionTestUtils.getField(MessageResolver.class, "MESSAGE_SOURCE");
	}
}
