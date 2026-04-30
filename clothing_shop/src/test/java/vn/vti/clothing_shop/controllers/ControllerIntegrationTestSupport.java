package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.JwtService;
import vn.vti.clothing_shop.services.impl.OrderItemServiceImpl;
import vn.vti.clothing_shop.services.interfaces.AuditService;
import vn.vti.clothing_shop.services.interfaces.BrandService;
import vn.vti.clothing_shop.services.interfaces.CategoryService;
import vn.vti.clothing_shop.services.interfaces.ChatService;
import vn.vti.clothing_shop.services.interfaces.CommentService;
import vn.vti.clothing_shop.services.interfaces.ImportedProductService;
import vn.vti.clothing_shop.services.interfaces.InputSaleService;
import vn.vti.clothing_shop.services.interfaces.OnSaleProductService;
import vn.vti.clothing_shop.services.interfaces.OrderService;
import vn.vti.clothing_shop.services.interfaces.PaymentService;
import vn.vti.clothing_shop.services.interfaces.ProductService;
import vn.vti.clothing_shop.services.interfaces.StatService;
import vn.vti.clothing_shop.services.interfaces.UserService;
import vn.vti.clothing_shop.services.interfaces.VoucherService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
abstract class ControllerIntegrationTestSupport {
	private static final String USER_TOKEN = "user-token";
	private static final String ADMIN_TOKEN = "admin-token";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private AuditService auditService;

	@MockitoBean
	private BrandService brandService;

	@MockitoBean
	private CategoryService categoryService;

	@MockitoBean
	private ChatService chatService;

	@MockitoBean
	private CommentService commentService;

	@MockitoBean
	private ImportedProductService importedProductService;

	@MockitoBean
	private InputSaleService inputSaleService;

	@MockitoBean
	private OnSaleProductService onSaleProductService;

	@MockitoBean
	private OrderItemServiceImpl orderItemService;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private PaymentService paymentService;

	@MockitoBean
	private ProductService productService;

	@MockitoBean
	private StatService statService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private VoucherService voucherService;

	protected static ApiEndpoint endpoint(
			String name,
			HttpMethod method,
			String path,
			String body,
			int expectedStatus,
			Auth auth,
			boolean protectedEndpoint,
			boolean adminEndpoint) {
		return new ApiEndpoint(name, method, path, body, expectedStatus, auth, protectedEndpoint, adminEndpoint);
	}

	protected static String brandCreateJson() {
		return """
				{"name":"Brand","description":"Description"}
				""";
	}

	protected static String brandUpdateJson() {
		return """
				{"name":"Brand","description":"Description","version":0}
				""";
	}

	protected static String categoryCreateJson() {
		return """
				{"name":"Category","description":"Description"}
				""";
	}

	protected static String categoryUpdateJson() {
		return """
				{"name":"Category","description":"Description","version":0}
				""";
	}

	protected static String chatCreateJson() {
		return """
				{"content":"Hello"}
				""";
	}

	protected static String chatUpdateJson() {
		return """
				{"content":"Updated","version":0}
				""";
	}

	protected static String chatReplyJson() {
		return """
				{"reply":"Reply"}
				""";
	}

	protected static String commentCreateJson() {
		return """
				{"productId":1,"content":"Nice","star":5}
				""";
	}

	protected static String commentUpdateJson() {
		return """
				{"productId":1,"content":"Updated","star":4,"version":0}
				""";
	}

	protected static String importedProductCreateJson() {
		return """
				{
				  "productId":1,
				  "code":"#112233",
				  "name":"Variant",
				  "size":"M",
				  "height":"170",
				  "weight":"60",
				  "material":"Cotton",
				  "gender":"UNISEX",
				  "importPrice":100,
				  "imageUrl":"https://example.com/image.jpg",
				  "sliderUrl1":"https://example.com/1.jpg",
				  "sliderUrl2":"https://example.com/2.jpg",
				  "sliderUrl3":"https://example.com/3.jpg",
				  "sliderUrl4":"https://example.com/4.jpg",
				  "publicIdUrl":"image",
				  "publicIdSliderUrl1":"s1",
				  "publicIdSliderUrl2":"s2",
				  "publicIdSliderUrl3":"s3",
				  "publicIdSliderUrl4":"s4",
				  "importNumber":10
				}
				""";
	}

	protected static String importedProductUpdateJson() {
		return """
				{
				  "productId":1,
				  "colorId":1,
				  "sizeId":1,
				  "materialId":1,
				  "code":"#112233",
				  "name":"Variant",
				  "size":"M",
				  "height":"170",
				  "weight":"60",
				  "material":"Cotton",
				  "gender":"UNISEX",
				  "importPrice":100,
				  "importNumber":10,
				  "imageUrl":"https://example.com/image.jpg",
				  "sliderUrl1":"https://example.com/1.jpg",
				  "sliderUrl2":"https://example.com/2.jpg",
				  "sliderUrl3":"https://example.com/3.jpg",
				  "sliderUrl4":"https://example.com/4.jpg",
				  "publicIdUrl":"image",
				  "publicIdSliderUrl1":"s1",
				  "publicIdSliderUrl2":"s2",
				  "publicIdSliderUrl3":"s3",
				  "publicIdSliderUrl4":"s4",
				  "version":0
				}
				""";
	}

	protected static String inputSaleCreateJson() {
		return """
				{"filter":"PRODUCT","filterId":1,"salePercentage":10,"discount":5,"availableDate":"2030-01-01","endDate":"2030-02-01"}
				""";
	}

	protected static String inputSaleUpdateJson() {
		return """
				{"salePercentage":10,"discount":5,"availableDate":"2030-01-01","endDate":"2030-02-01","version":1}
				""";
	}

	protected static String orderUpdateJson() {
		return """
				{"address":"Address","phoneNumber":"84901234567","receiverName":"Receiver","isPresent":false,"paymentMethod":"COD","voucherId":1}
				""";
	}

	protected static String orderCheckoutJson() {
		return """
				{"orderId":1}
				""";
	}

	protected static String orderConfirmJson() {
		return """
				{"orderCode":123,"status":true}
				""";
	}

	protected static String orderItemCreateJson() {
		return """
				{"productId":1,"orderId":1,"quantity":1}
				""";
	}

	protected static String orderItemUpdateJson() {
		return """
				{"productId":1,"quantity":2,"version":1}
				""";
	}

	protected static String productCreateJson() {
		return """
				{"name":"Product","shortDescription":"Short","categoryId":1,"brandId":1}
				""";
	}

	protected static String productUpdateJson() {
		return """
				{"name":"Product","shortDescription":"Short","categoryId":1,"brandId":1,"version":0}
				""";
	}

	protected static String userLoginJson() {
		return """
				{"usernameOrEmailOrPhoneNumber":"user","password":"password"}
				""";
	}

	protected static String userCreateJson() {
		return """
				{
				  "name":"User",
				  "username":"user",
				  "password":"password",
				  "email":"user@example.com",
				  "phoneNumber":"84901234567",
				  "address":"Address",
				  "birthday":"2000-01-01",
				  "avatarUrl":"https://example.com/avatar.jpg",
				  "publicIdAvatarUrl":"avatar",
				  "gender":"MALE"
				}
				""";
	}

	protected static String userUpdateJson() {
		return """
				{
				  "name":"User",
				  "email":"user@example.com",
				  "phoneNumber":"84901234567",
				  "address":"Address",
				  "birthday":"2000-01-01",
				  "avatarUrl":"https://example.com/avatar.jpg",
				  "publicIdAvatarUrl":"avatar",
				  "gender":"MALE"
				}
				""";
	}

	protected static String userPasswordJson() {
		return """
				{"oldPassword":"old-password","password":"new-password","version":0}
				""";
	}

	protected static String voucherCreateJson() {
		return """
				{"code":"SAVE10","inputStock":10,"value":10,"availableDate":"2030-01-01","endDate":"2030-02-01"}
				""";
	}

	protected static String voucherUpdateJson() {
		return """
				{"code":"SAVE10","inputStock":10,"value":10,"availableDate":"2030-01-01","endDate":"2030-02-01","version":0}
				""";
	}

	@BeforeEach
	void setUpAuthentication() {
		User user = user(1L, UserRole.USER);
		User admin = user(2L, UserRole.ADMIN);

		when(jwtService.isTokenExpired(USER_TOKEN)).thenReturn(false);
		when(jwtService.isTokenExpired(ADMIN_TOKEN)).thenReturn(false);
		when(jwtService.extractId(USER_TOKEN)).thenReturn("1");
		when(jwtService.extractId(ADMIN_TOKEN)).thenReturn("2");
		when(jwtService.isTokenValid(eq(USER_TOKEN), any(User.class))).thenReturn(true);
		when(jwtService.isTokenValid(eq(ADMIN_TOKEN), any(User.class))).thenReturn(true);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
	}

	private static User user(Long id, UserRole role) {
		User user = new User();
		user.setId(id);
		user.setName(role.name().toLowerCase());
		user.setUsername(role.name().toLowerCase());
		user.setRole(role);
		user.setGender(UserGender.MALE);
		user.setSalt("salt-" + id);
		user.setPhoneNumber("8490123456" + id);
		return user;
	}

	protected void shouldReach(ApiEndpoint endpoint) throws Exception {
		mockMvc.perform(request(endpoint.method(), endpoint.path())
				                .contentType(MediaType.APPLICATION_JSON)
				                .accept(MediaType.APPLICATION_JSON)
				                .content(endpoint.body() == null ? "" : endpoint.body())
				                .headers(headers(endpoint.auth())))
		       .andExpect(status().is(endpoint.expectedStatus()));
	}

	private static HttpHeaders headers(Auth auth) {
		HttpHeaders headers = new HttpHeaders();
		if (auth == Auth.USER) {
			headers.setBearerAuth(USER_TOKEN);
		} else if (auth == Auth.ADMIN) {
			headers.setBearerAuth(ADMIN_TOKEN);
		}
		return headers;
	}

	protected void shouldRejectWithoutToken(ApiEndpoint endpoint) throws Exception {
		mockMvc.perform(request(endpoint.method(), endpoint.path())
				                .contentType(MediaType.APPLICATION_JSON)
				                .accept(MediaType.APPLICATION_JSON)
				                .content(endpoint.body() == null ? "" : endpoint.body()))
		       .andExpect(status().isUnauthorized());
	}

	protected void shouldRejectUser(ApiEndpoint endpoint) throws Exception {
		mockMvc.perform(request(endpoint.method(), endpoint.path())
				                .contentType(MediaType.APPLICATION_JSON)
				                .accept(MediaType.APPLICATION_JSON)
				                .content(endpoint.body() == null ? "" : endpoint.body())
				                .headers(headers(Auth.USER)))
		       .andExpect(status().isForbidden());
	}

	protected enum Auth {
		NONE,
		USER,
		ADMIN
	}

	protected record ApiEndpoint(
			String name,
			HttpMethod method,
			String path,
			String body,
			int expectedStatus,
			Auth auth,
			boolean protectedEndpoint,
			boolean adminEndpoint) {
		@Override
		public String toString() {
			return name;
		}
	}
}
