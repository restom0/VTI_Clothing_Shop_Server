package vn.vti.clothing_shop.services.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.outs.AuditDTO;
import vn.vti.clothing_shop.entities.Audit;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.AuditMapper;
import vn.vti.clothing_shop.mappers.ChatMapper;
import vn.vti.clothing_shop.mappers.CommentMapper;
import vn.vti.clothing_shop.repositories.AuditRepository;
import vn.vti.clothing_shop.repositories.ChatRepository;
import vn.vti.clothing_shop.repositories.CommentRepository;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatCommentAuditStatServiceTest {
	@Test
	void containsChatCommentAuditAndStatServiceTests() {
		assertThat(List.of("audit", "chat", "comment", "stat")).hasSize(4);
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class AuditServiceTest {
		@Mock
		AuditMapper auditMapper;

		@Mock
		AuditRepository auditRepository;

		@Mock
		UserRepository userRepository;

		@InjectMocks
		AuditServiceImpl service;

		@Test
		void createAuditSavesMappedAudit() throws WrapperException {
			AuditDTO dto = new AuditDTO();
			User user = new User();
			Audit audit = new Audit();

			when(userRepository.findById(1L)).thenReturn(Optional.of(user));
			when(auditMapper.dtoToEntity(dto, user)).thenReturn(audit);

			service.createAudit(dto, 1L);

			verify(auditRepository).save(audit);
		}

		@Test
		void createAuditWrapsMissingUser() {
			when(userRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.createAudit(new AuditDTO(), 1L))
					.isInstanceOf(WrapperException.class);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class ChatServiceTest {
		@Mock
		ChatRepository chatRepository;

		@Mock
		UserRepository userRepository;

		@Mock
		ChatMapper chatMapper;

		@InjectMocks
		ChatServiceImpl service;

		@Test
		void replyChatSavesReplyAndUpdatesOriginalChat() throws WrapperException {
			User user = new User();
			Chat original = new Chat();
			Chat reply = new Chat();
			ChatReplyRequest request = new ChatReplyRequest("hello");

			when(userRepository.findByDeletedAtIsNullAndId(9L)).thenReturn(Optional.of(user));
			when(chatRepository.findByDeletedAtIsNullAndId(1L)).thenReturn(Optional.of(original));
			when(chatMapper.replyRequestToEntity(request, user)).thenReturn(reply);

			service.replyChat(1L, 9L, request);

			assertThat(original.getReply()).isSameAs(reply);
			verify(chatRepository).save(reply);
			verify(chatRepository).save(original);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class CommentServiceTest {
		@Mock
		CommentRepository commentRepository;

		@Mock
		ProductRepository productRepository;

		@Mock
		UserRepository userRepository;

		@Mock
		CommentMapper commentMapper;

		@InjectMocks
		CommentServiceImpl service;

		@Test
		void createCommentSavesMappedComment() throws WrapperException {
			CommentCreateRequest request = new CommentCreateRequest(1L, "good", 5F);
			Product product = new Product();
			User user = new User();
			Comment comment = new Comment();

			when(productRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(product));
			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(commentMapper.createRequestToEntity(request, user, product)).thenReturn(comment);

			service.createComment(9L, request);

			verify(commentRepository).save(comment);
		}

		@Test
		void createCommentWrapsMissingProduct() {
			CommentCreateRequest request = new CommentCreateRequest(1L, "good", 5F);
			when(productRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.createComment(9L, request))
					.isInstanceOf(WrapperException.class);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class StatServiceTest {
		@Mock
		OrderRepository orderRepository;

		@Mock
		UserRepository userRepository;

		@Mock
		OrderItemRepository orderItemRepository;

		@InjectMocks
		StatServiceImpl service;

		@Test
		void getStatCollectsDashboardCounters() {
			when(orderRepository.sumTotalPriceByDeletedAtIsNullAndPaymentStatus(PaymentStatus.COMPLETED)).thenReturn(1000L);
			when(orderRepository.count()).thenReturn(3L);
			when(orderRepository.countByDeletedAtIsNullAndPaymentStatus(PaymentStatus.COMPLETED)).thenReturn(2L);
			when(userRepository.countByDeletedAtIsNullAndRole(UserRole.USER)).thenReturn(4L);
			when(orderItemRepository.countByDeletedAtIsNullAndOrder_PaymentStatus(PaymentStatus.COMPLETED)).thenReturn(5L);

			Map<String, Long> stat = service.getStat();

			assertThat(stat).containsEntry("income", 1000L)
			                .containsEntry("order", 3L)
			                .containsEntry("completed", 2L)
			                .containsEntry("user", 4L)
			                .containsEntry("product", 5L);
		}

		@Test
		void getMonthlyIncomeForLastFiveYearsReturnsTwelveMonthsEach() {
			int currentYear = LocalDate.now().getYear();
			when(orderRepository.sumTotalPriceByMonthAndYear(1, currentYear)).thenReturn(100L);

			Map<Integer, ArrayList<Long>> result = service.getMonthlyIncomeForLast5Years();

			assertThat(result).hasSize(5);
			assertThat(result.get(currentYear)).hasSize(12);
			assertThat(result.get(currentYear).get(0)).isEqualTo(100L);
		}
	}
}
