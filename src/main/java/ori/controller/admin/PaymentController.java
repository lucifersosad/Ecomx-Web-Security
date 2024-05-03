package ori.controller.admin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;
import ori.config.scurity.AuthUser;
import ori.entity.Cart;
import ori.entity.Order;
import ori.entity.OrderDetail;
import ori.entity.OrderDetailKey;
import ori.entity.Product;
import ori.entity.Promotion;
import ori.entity.User;
import ori.model.Response;
import ori.service.ICartService;
import ori.service.IOrderDetailService;
import ori.service.IOrderService;
import ori.service.IProductService;
import ori.service.IPromotionServive;
import ori.service.IUserService;

@Controller
@RequestMapping("/payment")
public class PaymentController {
	@Autowired
	IUserService userService;
	@Autowired
	IOrderService orderService;
	@Autowired
	IOrderDetailService orderDetailService;
	@Autowired
	ICartService cartService;
	@Autowired
	IProductService productService;
	@Autowired
	IPromotionServive promoService;

	@GetMapping("/vnpay/option")
	public String list(ModelMap model, @Validated @RequestParam("amount") double amount) {
		// int amount = 20000;
		model.addAttribute("amount", amount);
		return "web/vnpay/vnpay_pay";
	}

	@GetMapping("/paypal/create")
	public RedirectView createPayPalPayment(@RequestParam(name = "discount", defaultValue = "0") Double discount) {
		System.out.println("=============================" + discount + "========================================");
		User user = userService.getUserLogged();
		RestTemplate restTemplate = new RestTemplate();
		String apiUrl = "http://localhost:8888/api/payment/paypal/create?userId=" + user.getUserId() + "&discount=" + discount; // Thay đổi URL
																										// API cần gọi
		ResponseEntity<Response> response = restTemplate.getForEntity(apiUrl, Response.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			Object responseBody = response.getBody().getBody(); // Lấy body từ Response, nơi chứa URL
			if (responseBody instanceof String) {
				String redirectUrl = (String) responseBody; // Ép kiểu sang String nếu là URL
				return new RedirectView(redirectUrl); // Chuyển hướng trình duyệt tới URL từ API
			}
		}
		return new RedirectView("/"); // Chuyển hướng đến trang lỗi
	}

	@GetMapping("/paypal/success")
	public String successPayPalPayment(@RequestParam("paymentId") String paymentId,
			@RequestParam("PayerID") String payerId,
			HttpSession session) {
		User user = userService.getUserLogged();
		RestTemplate restTemplate = new RestTemplate();
		String apiUrl = "http://localhost:8888/api/payment/paypal/success?paymentId=" + paymentId + "&PayerID="
				+ payerId + "&userId=" + user.getUserId(); // Thay đổi URL API cần gọi
		ResponseEntity<Response> response = restTemplate.getForEntity(apiUrl, Response.class);
		String redirectUrl = "";
		if (response.getStatusCode().is2xxSuccessful()) {
			Object responseBody = response.getBody().getBody(); // Lấy body từ Response, nơi chứa URL
			if (responseBody instanceof String) {
				redirectUrl = (String) responseBody; // Ép kiểu sang String nếu là URL
				// Chuyển hướng trình duyệt tới URL từ API
			}
		}
		Promotion promotion = (Promotion) session.getAttribute("promoCode");
		if (promotion != null) {
			promotion.setIs_active(0);
			promoService.save(promotion);
			session.removeAttribute("promoCode");
		}
		return redirectUrl;
	}

	@GetMapping("/paypal/cancel")
	public String cancelPayPalPayment(@RequestParam("paymentId") String paymentId,
			@RequestParam("PayerID") String payerId) {

		RestTemplate restTemplate = new RestTemplate();
		String apiUrl = "http://localhost:8888/api/payment/paypal/cancel"; // Thay đổi URL API cần gọi
		ResponseEntity<Response> response = restTemplate.getForEntity(apiUrl, Response.class);
		String redirectUrl = "";
		if (response.getStatusCode().is2xxSuccessful()) {
			Object responseBody = response.getBody().getBody(); // Lấy body từ Response, nơi chứa URL
			if (responseBody instanceof String) {
				redirectUrl = (String) responseBody; // Ép kiểu sang String nếu là URL
				// Chuyển hướng trình duyệt tới URL từ API
			}
		}
		return redirectUrl;
	}

	@GetMapping("/vnpay/return")
	public String returnVNPAYPayment(ModelMap model, @RequestParam Map<String, String> queryParams, HttpSession session) {
		User user = userService.getUserLogged();
		RestTemplate restTemplate = new RestTemplate();
		StringBuilder queryString = new StringBuilder();
		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
			if (queryString.length() != 0) {
				queryString.append("&");
			}
			queryString.append(entry.getKey()).append("=").append(entry.getValue());
		}
		String apiUrl = "http://localhost:8888/api/payment/vnpay/return?userId=" + user.getUserId();
		System.out.println("==============" + apiUrl + "==============");
		if (queryString.length() > 0) {
			apiUrl += "&" + queryString.toString();
		}
		System.out.println(apiUrl);
		ResponseEntity<Response> response = restTemplate.getForEntity(apiUrl, Response.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			boolean status = response.getBody().getStatus();
			if (status) {
				Promotion promotion = (Promotion) session.getAttribute("promoCode");
				if (promotion != null) {
					promotion.setIs_active(0);
					promoService.save(promotion);
					session.removeAttribute("promoCode");
				}
				return "web/vnpay/vnpay_success";
			} else {
				Object responseBody = response.getBody().getBody();
				if (responseBody instanceof String) {
					model.addAttribute("message", (String) responseBody);
				}
				return "web/vnpay/vnpay_cancel";
			}
		}
		return "/";
	}

	@GetMapping("/cod")
	public String codPayment(@Validated @RequestParam("amount") double amount, HttpSession session) {
		User user = userService.getUserLogged();
		Order order = new Order();
		order.setUserId(user);
		order.setCurrency("VND");
		order.setTotal(amount);
		order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
		order.setPayment_method("COD");
		order.setStatus(0);
		orderService.save(order);
		List<Order> orders = orderService.findAll();
		Order lastOrder = orders.get(orders.size() - 1);
		List<Cart> carts = cartService.findByUserId(user.getUserId());
		for (Cart cart : carts) {
			OrderDetailKey orderDetailKey = new OrderDetailKey();
			orderDetailKey.setOrderId(lastOrder.getOrderId()); // Set the appropriate orderId
			orderDetailKey.setProId(cart.getProduct().getProId()); // Set the appropriate proId
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setId(orderDetailKey);
			orderDetail.setOrder(lastOrder);
			orderDetail.setProduct(cart.getProduct());
			orderDetail.setQuantity(cart.getQuantity());
			orderDetail.setDiscount(cart.getProduct().getSale());
			orderDetailService.save(orderDetail);
			Optional<Product> optPro = productService.findById(cart.getProduct().getProId());
			Product pro = new Product();
			if (optPro.isPresent()) {
				pro = optPro.get();
			}
			pro.setStock(pro.getStock() - cart.getQuantity());
			productService.save(pro);
			cartService.delete(cart);
		}
		Promotion promotion = (Promotion) session.getAttribute("promoCode");
		if (promotion != null) {
			promotion.setIs_active(0);
			promoService.save(promotion);
			session.removeAttribute("promoCode");
		}
		return "web/codSuccess";
	}
}
