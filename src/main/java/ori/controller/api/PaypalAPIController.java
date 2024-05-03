package ori.controller.api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.api.payments.Links;

import ori.config.PaypalConfig;
import ori.config.scurity.AuthUser;
import ori.entity.Cart;
import ori.entity.Order;
import ori.entity.OrderDetail;
import ori.entity.OrderDetailKey;
import ori.entity.Product;
import ori.entity.User;
import ori.model.Response;
import ori.service.ICartService;
import ori.service.IOrderDetailService;
import ori.service.IOrderService;
import ori.service.IProductService;
import ori.service.IUserService;
import ori.service.PaypalService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@RestController
@RequestMapping("/api/payment/paypal")
public class PaypalAPIController {
	@Autowired
	PaypalService service;
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

	public static final double unit = (double) 0.000041;

	@GetMapping("/create")
	public ResponseEntity<?> payment(
			@RequestParam("userId") Integer userId,
			@RequestParam("discount") Double discount
			) {
		try {
			System.out.println("=============================" + discount + "========================================");
			Payment payment = service.createPayment(userId, discount, "USD", "PAYPAL", "SALE", "I buy it",
					PaypalConfig.CANCEL_URL, PaypalConfig.SUCCESS_URL);
			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					return new ResponseEntity<Response>(new Response(true, "success", link.getHref()), HttpStatus.OK);
				}
			}

		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Response>(new Response(false, "fail", "Khong the tao link bay gio"), HttpStatus.OK);
	}

	@GetMapping("/cancel")
	public ResponseEntity<?> cancelPay() {
		return new ResponseEntity<Response>(new Response(true, "success", "web/paypal/cancel"), HttpStatus.OK);
	}

	@GetMapping("/success")
	public ResponseEntity<?> successPay(@RequestParam("paymentId") String paymentId,
			@RequestParam("PayerID") String payerId, @RequestParam("userId") Integer userId) {

		try {
			Payment payment = service.executePayment(paymentId, payerId);
			if (payment.getState().equals("approved")) {
				String jsonResult = payment.toJSON();
				JSONParser parser = new JSONParser();
				Order order = new Order();
				try {
					// Phân tích chuỗi JSON
					JSONObject paymentObject = (JSONObject) parser.parse(jsonResult);

					// Truy cập đến transactions
					JSONArray transactions = (JSONArray) paymentObject.get("transactions");

					// Lặp qua các giao dịch
					for (Object transactionObj : transactions) {
						JSONObject transaction = (JSONObject) transactionObj;

						// Truy cập đến related_resources
						JSONArray relatedResources = (JSONArray) transaction.get("related_resources");

						// Lặp qua các related_resources
						for (Object resourceObj : relatedResources) {
							JSONObject resource = (JSONObject) resourceObj;
							JSONObject sale = (JSONObject) resource.get("sale");
							JSONObject amount = (JSONObject) sale.get("amount");
							String amountTotal = (String) amount.get("total");
							order.setTotal(Double.parseDouble(amountTotal));
							order.setCurrency("USD");
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

				Optional<User> optUser = userService.findById(userId);
				if (optUser.isPresent()) {
					User user = optUser.get();
					order.setUserId(user);
					String inputDateTimeString = payment.getCreateTime();

					// Chuyển đổi chuỗi sang đối tượng LocalDateTime (thời gian theo UTC)
					ZonedDateTime utcDateTime = ZonedDateTime.parse(inputDateTimeString);

			        // Chuyển múi giờ từ UTC sang Etc/GMT+7
			        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
			        ZonedDateTime vietnamDateTime  = utcDateTime.withZoneSameInstant(vietnamTimeZone);

			        // Định dạng lại theo định dạng dd/MM/yyyy HH:mm:ss
			        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			        String formattedDateTime = vietnamDateTime.format(formatter);
					order.setDate(formattedDateTime);
					order.setPayment_method("Paypal");
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
				}
				return new ResponseEntity<Response>(new Response(true, "success", "web/paypal/success"), HttpStatus.OK);

			}
		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}
		return new ResponseEntity<Response>(new Response(false, "fail", "web/paypal/cancel"), HttpStatus.OK);
	}
}