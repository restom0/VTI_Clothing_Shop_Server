package vn.vti.clothing_shop.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
public class OpenAPISecurityConfiguration {
	private static final String BEARER_AUTH = "bearerAuth";
	private static final String AUTH_TAG = "Authentication & Users";
	private static final String CATALOG_TAG = "Catalog";
	private static final String COMMERCE_TAG = "Commerce";
	private static final String INVENTORY_TAG = "Inventory";
	private static final String ENGAGEMENT_TAG = "Engagement";
	private static final String REPORTING_TAG = "Reporting";
	private static final String ADMIN_TAG = "Administration";
	private static final String OPERATIONS_TAG = "Operations";
	private static final String ERROR_PATH = "/error";
	private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "es", "de", "fr", "ca", "it");

	/** Handles clothing shop open API. */
	@Bean
	public OpenAPI clothingShopOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						      .title("VTI Clothing Shop API")
						      .version("1.0.0")
						      .description("REST API for the VTI Clothing Shop backend. "
								                   + "Use the Authorization button with a JWT returned by `/user/login` "
								                   + "or OAuth2 social login. Responses support localized messages through "
								                   + "the `Accept-Language` header.")
						      .contact(new Contact()
								               .name("VTI Clothing Shop Team")
								               .email("support@vti-clothing-shop.local")
								               .url("https://github.com/"))
						      .license(new License()
								               .name("Apache License 2.0")
								               .url("https://www.apache.org/licenses/LICENSE-2.0")))
				.servers(List.of(
						new Server().url("http://localhost:8080").description("Local Spring Boot API"),
						new Server().url("http://localhost:8088").description("Local Nginx API Gateway"),
						new Server().url("https://vti-clothing-shop.onrender.com").description("Production API")
				))
				.externalDocs(new ExternalDocumentation()
						              .description("Project README")
						              .url("https://github.com/"))
				.components(new Components()
						            .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
								            .type(SecurityScheme.Type.HTTP)
								            .scheme("bearer")
								            .bearerFormat("JWT")
								            .description("Paste the JWT without the `Bearer` prefix.")))
				.tags(List.of(
						new Tag().name(AUTH_TAG).description("Local login, social login, profile and account management."),
						new Tag().name(CATALOG_TAG).description("Products, brands, categories, vouchers and sale catalog APIs."),
						new Tag().name(COMMERCE_TAG).description("Cart, checkout, orders, order items and payment confirmation."),
						new Tag().name(INVENTORY_TAG).description("Input sale and imported product management."),
						new Tag().name(ENGAGEMENT_TAG).description("Chat and comment APIs."),
						new Tag().name(REPORTING_TAG).description("Statistics and business reporting APIs."),
						new Tag().name(ADMIN_TAG).description("Audit and administrative APIs."),
						new Tag().name(OPERATIONS_TAG).description("Health, actuator and operational endpoints.")
				));
	}

	/** Handles professional open API customizer. */
	@Bean
	public OpenApiCustomizer professionalOpenApiCustomizer() {
		return openApi -> {
			addOAuth2AuthorizationPaths(openApi);
			if (openApi.getPaths() == null) {
				return;
			}
			openApi.getPaths().forEach((path, pathItem) -> {
				if (pathItem == null) {
					return;
				}
				pathItem.readOperationsMap().forEach((method, operation) -> polishOperation(path, method, operation));
			});
		};
	}

	/** Handles all API group. */
	@Bean
	public GroupedOpenApi allApiGroup() {
		return GroupedOpenApi.builder()
		                     .group("01 - All APIs")
		                     .pathsToMatch("/**")
		                     .pathsToExclude(ERROR_PATH)
		                     .build();
	}

	/** Handles authentication API group. */
	@Bean
	public GroupedOpenApi authenticationApiGroup() {
		return GroupedOpenApi.builder()
		                     .group("02 - Authentication")
		                     .pathsToMatch("/user/**", "/oauth2/**", "/login/oauth2/**")
		                     .pathsToExclude(ERROR_PATH)
		                     .build();
	}

	/** Handles catalog API group. */
	@Bean
	public GroupedOpenApi catalogApiGroup() {
		return GroupedOpenApi.builder()
		                     .group("03 - Catalog")
		                     .pathsToMatch("/product/**", "/brand/**", "/category/**", "/voucher/**", "/on-sale-product/**")
		                     .pathsToExclude(ERROR_PATH)
		                     .build();
	}

	/** Handles commerce API group. */
	@Bean
	public GroupedOpenApi commerceApiGroup() {
		return GroupedOpenApi.builder()
		                     .group("04 - Commerce")
		                     .pathsToMatch("/order/**", "/order-item/**", "/order-items/**")
		                     .pathsToExclude(ERROR_PATH)
		                     .build();
	}

	/** Handles operations API group. */
	@Bean
	public GroupedOpenApi operationsApiGroup() {
		return GroupedOpenApi.builder()
		                     .group("05 - Operations")
		                     .pathsToMatch("/stat/**", "/audit/**", "/actuator/**")
		                     .pathsToExclude(ERROR_PATH)
		                     .build();
	}

	/** Polishes operation. */
	private void polishOperation(String path, PathItem.HttpMethod method, Operation operation) {
		if (operation == null) {
			return;
		}

		String tag = tagFor(path);
		if (operation.getTags() == null || operation.getTags().isEmpty() || hasGeneratedControllerTag(operation)) {
			operation.setTags(List.of(tag));
		}
		if (operation.getSummary() == null || operation.getSummary().isBlank()) {
			operation.setSummary(summaryFor(path, method));
		}
		if (operation.getDescription() == null || operation.getDescription().isBlank()) {
			operation.setDescription(descriptionFor(path, method));
		}

		addLanguageHeader(operation);
		addStandardResponses(path, method, operation);
		if (requiresAuthentication(path, method)) {
			operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
		}
	}

	/** Adds OAuth 2 authorization paths. */
	private void addOAuth2AuthorizationPaths(OpenAPI openApi) {
		Paths paths = openApi.getPaths();
		if (paths == null) {
			paths = new Paths();
			openApi.setPaths(paths);
		}
		addOAuth2AuthorizationPath(paths, "google", "Google");
		addOAuth2AuthorizationPath(paths, "facebook", "Facebook");
		addOAuth2AuthorizationPath(paths, "twitter", "Twitter/X");
	}

	/** Adds OAuth 2 authorization path. */
	private void addOAuth2AuthorizationPath(Paths paths, String registrationId, String providerName) {
		String path = "/oauth2/authorization/" + registrationId;
		if (paths.containsKey(path)) {
			return;
		}
		Operation operation = new Operation()
				.tags(List.of(AUTH_TAG))
				.summary("Start " + providerName + " OAuth2 login")
				.description("Redirects the browser to " + providerName + " and returns to `/login/oauth2/code/" + registrationId
						             + "`.")
				.responses(new ApiResponses()
						           .addApiResponse("302", new ApiResponse().description(
								           "Redirect to " + providerName + " authorization page"))
						           .addApiResponse("429", new ApiResponse().description("Rate limit exceeded"))
						           .addApiResponse("500", new ApiResponse().description("Unexpected server error")));
		paths.addPathItem(path, new PathItem().get(operation));
	}

	/** Checks whether generated controller tag. */
	private boolean hasGeneratedControllerTag(Operation operation) {
		return operation.getTags().stream()
		                .map(tag -> tag.toLowerCase(Locale.ROOT))
		                .anyMatch(tag -> tag.endsWith("-controller") || tag.contains("controller"));
	}

	/** Adds language header. */
	private void addLanguageHeader(Operation operation) {
		boolean exists = operation.getParameters() != null && operation.getParameters().stream()
		                                                               .anyMatch(parameter -> "Accept-Language".equalsIgnoreCase(
				                                                               parameter.getName()));
		if (exists) {
			return;
		}

		StringSchema languageSchema = new StringSchema();
		languageSchema.setEnum(SUPPORTED_LANGUAGES);
		languageSchema.setDefault("en");

		operation.addParametersItem(new HeaderParameter()
				                            .name("Accept-Language")
				                            .description("Response language: en, es, de, fr, ca or it.")
				                            .required(false)
				                            .schema(languageSchema));
	}

	/** Adds standard responses. */
	private void addStandardResponses(String path, PathItem.HttpMethod method, Operation operation) {
		ApiResponses responses = operation.getResponses();
		if (responses == null) {
			responses = new ApiResponses();
			operation.setResponses(responses);
		}

		addResponseIfMissing(responses, "400", "Invalid request payload or parameters");
		if (requiresAuthentication(path, method)) {
			addResponseIfMissing(responses, "401", "Missing, expired or invalid JWT");
		}
		if (requiresAdmin(path)) {
			addResponseIfMissing(responses, "403", "Admin role required");
		}
		if (path.contains("{")) {
			addResponseIfMissing(responses, "404", "Resource not found");
		}
		addResponseIfMissing(responses, "429", "Rate limit exceeded");
		addResponseIfMissing(responses, "500", "Unexpected server error");
	}

	/** Adds response if missing. */
	private void addResponseIfMissing(ApiResponses responses, String status, String description) {
		if (!responses.containsKey(status)) {
			responses.addApiResponse(status, new ApiResponse().description(description));
		}
	}

	/** Handles tag for. */
	private String tagFor(String path) {
		String normalizedPath = normalize(path);
		if (startsWithAny(normalizedPath, "/user", "/oauth2", "/login/oauth2")) {
			return AUTH_TAG;
		}
		if (startsWithAny(normalizedPath, "/order", "/order-item", "/order-items")) {
			return COMMERCE_TAG;
		}
		if (startsWithAny(normalizedPath, "/product", "/brand", "/category", "/voucher", "/on-sale-product")) {
			return CATALOG_TAG;
		}
		if (startsWithAny(normalizedPath, "/imported-product", "/input-sale")) {
			return INVENTORY_TAG;
		}
		if (startsWithAny(normalizedPath, "/chat", "/comment")) {
			return ENGAGEMENT_TAG;
		}
		if (startsWithAny(normalizedPath, "/stat")) {
			return REPORTING_TAG;
		}
		if (startsWithAny(normalizedPath, "/audit", "/log")) {
			return ADMIN_TAG;
		}
		return OPERATIONS_TAG;
	}

	/** Handles summary for. */
	private String summaryFor(String path, PathItem.HttpMethod method) {
		String normalizedPath = normalize(path);
		Map<String, String> explicitSummaries = Map.of(
				"POST /user/login", "Login with username, email or phone",
				"POST /user/register", "Register a customer account",
				"GET /user/profile", "Get current user profile",
				"PUT /user/password", "Change current user password",
				"GET /order/cart", "Create or fetch current cart",
				"POST /order/checkout", "Create payment checkout",
				"PUT /order/success", "Confirm successful payment",
				"PUT /order/cancel", "Confirm cancelled payment"
		);
		String explicit = explicitSummaries.get(method.name() + " " + normalizedPath);
		if (explicit != null) {
			return explicit;
		}

		String resource = resourceName(normalizedPath);
		return switch (method) {
			case GET -> normalizedPath.contains("{") ? "Get " + resource + " by id" : "List " + pluralize(resource);
			case POST -> "Create " + resource;
			case PUT, PATCH -> "Update " + resource;
			case DELETE -> "Delete " + resource;
			default -> method.name() + " " + normalizedPath;
		};
	}

	/** Handles description for. */
	private String descriptionFor(String path, PathItem.HttpMethod method) {
		String authHint = requiresAuthentication(path, method)
		                  ? " Requires a Bearer JWT."
		                  : " Public endpoint unless protected by deployment-level gateway rules.";
		return "Handles " + method.name() + " requests for `" + normalize(path) + "`." + authHint;
	}

	/** Handles resource name. */
	private String resourceName(String path) {
		String[] parts = path.replaceFirst("^/", "").split("/");
		if (parts.length == 0 || parts[0].isBlank()) {
			return "resource";
		}
		return parts[0].replace("-", " ");
	}

	/** Handles pluralize. */
	private String pluralize(String resource) {
		return resource.endsWith("s") ? resource : resource + "s";
	}

	/** Handles requires authentication. */
	private boolean requiresAuthentication(String path, PathItem.HttpMethod method) {
		String normalizedPath = normalize(path);
		if (requiresAdmin(normalizedPath)) {
			return true;
		}
		if (startsWithAny(normalizedPath, "/chat", "/order", "/order-item", "/order-items")) {
			return true;
		}
		if (normalizedPath.equals("/user/profile") || startsWithAny(normalizedPath, "/user/password")) {
			return true;
		}
		if (startsWithAny(normalizedPath, "/user") && isWriteMethod(method)) {
			return !normalizedPath.equals("/user/login") && !normalizedPath.equals("/user/register");
		}
		return isWriteMethod(method) && startsWithAny(normalizedPath,
		                                              "/brand",
		                                              "/category",
		                                              "/comment",
		                                              "/imported-product",
		                                              "/input-sale",
		                                              "/on-sale-product",
		                                              "/product",
		                                              "/voucher"
		);
	}

	/** Handles requires admin. */
	private boolean requiresAdmin(String path) {
		String normalizedPath = normalize(path);
		return startsWithAny(normalizedPath, "/audit", "/log");
	}

	/** Checks whether write method. */
	private boolean isWriteMethod(PathItem.HttpMethod method) {
		return method == PathItem.HttpMethod.POST
				|| method == PathItem.HttpMethod.PUT
				|| method == PathItem.HttpMethod.PATCH
				|| method == PathItem.HttpMethod.DELETE;
	}

	/** Handles starts with any. */
	private boolean startsWithAny(String path, String... prefixes) {
		for (String prefix : prefixes) {
			if (path.equals(prefix) || path.startsWith(prefix + "/")) {
				return true;
			}
		}
		return false;
	}

	/** Normalizes value. */
	private String normalize(String path) {
		if (path == null || path.isBlank()) {
			return "/";
		}
		String normalizedPath = path.toLowerCase(Locale.ROOT);
		if (normalizedPath.length() > 1 && normalizedPath.endsWith("/")) {
			normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
		}
		return normalizedPath;
	}
}
