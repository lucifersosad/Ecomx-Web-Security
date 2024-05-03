package ori.controller.api;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

import ori.config.VNPAYConfig;
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

@RestController
@RequestMapping("/api/payment/vnpay")
public class VNPAYAPIController {
	@Autowired
	IOrderService orderService;
	@Autowired
	IUserService userService;
	@Autowired
	IOrderDetailService orderDetailService;
	@Autowired
	ICartService cartService;
	@Autowired
	IProductService productService;

	@GetMapping(path = "/create")
	public ResponseEntity<?> createPayment(HttpServletRequest req, @Validated @RequestParam("amount") double amount)
			throws IOException {
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "other";
		int total = (int) amount * 100;
		String vnp_TxnRef = VNPAYConfig.getRandomNumber(8);
		String vnp_IpAddr = VNPAYConfig.getIpAddress(req);
		String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(total));
		vnp_Params.put("vnp_CurrCode", "VND");
		vnp_Params.put("vnp_BankCode", "NCB");
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + "001xx" + ". So tien:" + total);
		vnp_Params.put("vnp_OrderType", orderType);
		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", VNPAYConfig.vnp_ReturnUrl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.secretKey, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = VNPAYConfig.vnp_PayUrl + "?" + queryUrl;
		return new ResponseEntity<Response>(new Response(true, "success", paymentUrl), HttpStatus.OK);
	}

	@GetMapping("/return")
	public ResponseEntity<?> transactionAndReturn(@RequestParam Map<String, String> queryParams) {

		Map<String, String> fields = new HashMap<>();

		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
			String fieldName = entry.getKey();
			String fieldValue = entry.getValue();

			try {
				fieldName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());
				fieldValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());

				if (fieldValue != null && !fieldValue.isEmpty()) {
					fields.put(fieldName, fieldValue);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String vnp_SecureHash = queryParams.get("vnp_SecureHash");
		if (fields.containsKey("vnp_SecureHashType")) {
			fields.remove("vnp_SecureHashType");
		}
		if (fields.containsKey("vnp_SecureHash")) {
			fields.remove("vnp_SecureHash");
		}
		if (fields.containsKey("userId")) {
			fields.remove("userId");
		}
		String message = "";
		String signValue = VNPAYConfig.hashAllFields(fields);
		System.out.println(signValue);
		System.out.println(vnp_SecureHash);
		//signValue.equals(vnp_SecureHash)
		if (signValue.equals(vnp_SecureHash)) {
			boolean checkAmount = true; // Kiểm tra số tiền tài khoản so với tiền đơn hàng
			boolean checkOrderStatus = true; // Kiểm tra đơn hàng đã thanh toán hay chưa
			if (checkAmount) {
				if (checkOrderStatus) {
					if ("00".equals(queryParams.get("vnp_ResponseCode"))) {
						System.out.println("True");
						Optional<User> optUser = userService.findById(Integer.parseInt(queryParams.get("userId")));
						if (optUser.isPresent()) {
							User user = optUser.get();
							Order order = new Order();
							order.setUserId(user);
							DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
					        LocalDateTime dateTime = LocalDateTime.parse(queryParams.get("vnp_PayDate"), inputFormatter);
					        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					        String outputDateString = dateTime.format(outputFormatter);
							order.setDate(outputDateString);
							order.setPayment_method("VNPAY");
							order.setStatus(0);
							order.setTotal(Double.parseDouble(queryParams.get("vnp_Amount"))/100);
							order.setCurrency("VND");
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
							return new ResponseEntity<Response>(new Response(true, "success", ""), HttpStatus.OK);
						}
					} else {
						message = "Thanh toán không thành công";
					}
				} else {
					message = "Đơn hàng đã thanh toán";
				}
			} else {
				message = "Số tiền không hợp lệ";
			}
		} else {
			message = "Checksum không hợp lệ";
		}
		return new ResponseEntity<Response>(new Response(false, "fail", message), HttpStatus.OK);
	}
}