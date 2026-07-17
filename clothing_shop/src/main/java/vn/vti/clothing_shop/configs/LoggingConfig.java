package vn.vti.clothing_shop.configs;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConfig.class);

	/** Handles application layer. */
	@Pointcut("within(vn.vti.clothing_shop.controllers..*) || within(vn.vti.clothing_shop.services..*) || within(vn.vti.clothing_shop.repositories..*)")
	public void applicationLayer() {
	}

	/** Handles log unchecked exception. */
	@AfterThrowing(pointcut = "applicationLayer()", throwing = "exception")
	public void logUncheckedException(JoinPoint joinPoint, RuntimeException exception) {
		LOGGER.error("Unchecked exception at {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage(),
		             exception);
	}
}
