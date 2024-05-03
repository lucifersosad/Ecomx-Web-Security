package ori.controller.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import ori.entity.Cart;
import ori.entity.Product;
import ori.entity.Promotion;
import ori.entity.User;
import ori.model.CartModel;
import ori.model.ProductModel;
import ori.model.UserModel;
import ori.service.ICartService;
import ori.service.IPromotionServive;
import ori.service.IUserService;

@Controller
@RequestMapping("CheckOut")
public class CheckOutController {
	String PaymentMethod;
	String Note;
	@Autowired(required = true)
	IUserService userService;
	@Autowired(required = true)
	ICartService cartService;
	@Autowired
	IPromotionServive promoService;

	@GetMapping("")
	public String ThongtinKh(ModelMap model) {
		double sum = 0;
		User user = userService.getUserLogged();
		String add = user.getAddress();
		String[] parts = add.split("\\s*,\\s*");
		if (parts.length >= 3) {
			model.addAttribute("city", parts[3].trim());
			model.addAttribute("district", parts[2].trim());
			model.addAttribute("town", parts[1].trim());
			model.addAttribute("homeaddress", parts[0].trim());
		} else {
			model.addAttribute("homeaddress", add.trim());
		}
		model.addAttribute("user", user);

		List<Cart> list = cartService.findByUserId(user.getUserId());
		List<ProductModel> listp = new ArrayList<>();
		List<CartModel> listc = new ArrayList<>();
		List<Double> tong = new ArrayList<>();

		for (Cart cart : list) {
			Product pro = cart.getProduct();
			ProductModel productModel = new ProductModel();
			CartModel cartModel = new CartModel();
			productModel.setProId(pro.getProId());
			productModel.setImage_link(pro.getImage_link());
			productModel.setName(pro.getName());
			productModel.setPrice(Math.round(pro.getPrice() * (100 - pro.getSale()) / 100));
			cartModel.setQuantity(cart.getQuantity());
			double total = cartModel.getQuantity() * productModel.getPrice();
			tong.add(total);

			sum = sum + total;
			listp.add(productModel);
			listc.add(cartModel);
		}
		List<Map<String, Object>> CartList = new ArrayList<>();
		for (int i = 0; i < listp.size(); i++) {
			Map<String, Object> item = new HashMap<>();
			item.put("product", listp.get(i));
			item.put("quantity", listc.get(i));
			item.put("tong", tong.get(i));
			CartList.add(item);
		}
		model.addAttribute("list", CartList);
		model.addAttribute("total", sum);
		return "web/checkout";
	}

	@GetMapping("PaymentMethod")
	public String PaymentMethod(RedirectAttributes redirectAttributes, HttpSession session) {
		User user = userService.getUserLogged();
		List<Cart> carts = cartService.findByUserId(user.getUserId());
		//của giảm giá
		Promotion promotion = (Promotion) session.getAttribute("promoCode");
		double discountRate;
		
		if(promotion != null) {
			discountRate = (double) promotion.getDiscount_rate()/100;
		}
		else {
			discountRate = 0;
		}
		//
		int total = 0;
		for (Cart cart : carts) {
			int sale = cart.getProduct().getSale();
			int price = (int) Math.round(cart.getProduct().getPrice() * (100 - sale) / 100) * 1000;
			int quantity = cart.getQuantity();
			total += quantity * price;
		}
		total =  total - (int)(total * discountRate);
		if ("PayPal".equals(PaymentMethod)) {
			redirectAttributes.addAttribute("discount", discountRate);
			return "redirect:/payment/paypal/create";

		} else if ("VNPAY".equals(PaymentMethod)) {
			redirectAttributes.addAttribute("amount", String.valueOf(total));
			return "redirect:/payment/vnpay/option";

		} else {
			total += 30000;
			redirectAttributes.addAttribute("amount", String.valueOf(total));
			return "redirect:/payment/cod";
		}
	}

	@GetMapping("OrderNote")
	public String Note(ModelMap model) {
		model.addAttribute("note", Note);
		return "CheckOut/note";
	}

	@PostMapping("/Payment")
	public String processPayment(@RequestParam("paymentMethod") String paymentMethod) {
		PaymentMethod = paymentMethod;
		return "redirect:/CheckOut/PaymentMethod";
	}

	@PostMapping("/DiscountPost")
	public ResponseEntity<Double> DiscountPost(@RequestParam("promo") String promoCode, HttpSession session) {
		Promotion promotion = promoService.findpromotionByname(promoCode);
		int active = 1;
		double discountRate = 0;
		if(promotion != null) {
			active  = promotion.getIs_active();
		}
		else {
			discountRate = 2;
			return ResponseEntity.ok(discountRate);
		}
		if(active == 1) {
			discountRate = (double) promotion.getDiscount_rate()/100;
			session.setAttribute("promoCode", promotion);
		}
		return ResponseEntity.ok(discountRate);
	}
	@GetMapping("/RandomDiscount")
	public ResponseEntity<String> RandomDiscount(){
		String discountname = "ori";
		String countPromotion = String.valueOf(promoService.count()+ 1);
		int discount_rate = ThreadLocalRandom.current().nextInt(10,51);
		discountname = discountname + countPromotion + "sale" + String.valueOf(discount_rate);
		Promotion promo = new Promotion();
		promo.setName(discountname);
		promo.setDiscount_rate(discount_rate);
		promo.setDescription("Giam "+String.valueOf(discount_rate)+"%");
		promo.setIs_active(1);
		promoService.save(promo);
		return ResponseEntity.ok(discountname);
	}
}