package ori.controller.web;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ori.config.scurity.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ori.entity.Brand;
import ori.entity.Cart;
import ori.entity.Category;
import ori.entity.Product;
import ori.entity.User;
import ori.model.UserModel;
import ori.entity.Rating;
import ori.model.RatingModel;
import ori.service.IBrandService;
import ori.service.ICartService;
import ori.service.ICategoryService;
import ori.service.IProductService;
import ori.service.IRatingService;
import ori.service.IUserService;

@RequestMapping(value = {"", "/", "home"})
@Controller

public class HomeController {
	@Autowired
	ICategoryService categoryService;
	@Autowired
	IBrandService brandService;
	@Autowired
	IProductService productService;
	@Autowired
	IUserService userService;
	@Autowired
	ICartService cartService;
	@Autowired
	IRatingService ratingService;

	@GetMapping()
	public String trangchu(ModelMap model) {
		List<Product> productsMostSale = productService.findProductsMostSaleByCategory(); 
		List<Product> productsMostOrder = productService.findProductsMostOrder(); 
		List<Brand> brands = brandService.findAll();
		List<Category> categories = categoryService.findTop10(); 
		List<Rating> ratings = ratingService.findAllRatingDisplayed();
		User userLogged = userService.getUserLogged();
		if (userLogged != null) {
			List<Cart> list = cartService.findByUserId(userLogged.getUserId());
			model.addAttribute("cartQty", list.size());
		}
		model.addAttribute("brands", brands);
		model.addAttribute("categories", categories);
		model.addAttribute("products", productsMostOrder);
		model.addAttribute("ratings", ratings);
		return "web/index";
	}
	
	@GetMapping("/cartQty")
	@ResponseBody
	public int getCartQuantity() {
	    User userLogged = userService.getUserLogged();
	    if (userLogged != null) {
	    	int total = 0;
	        List<Cart> list = cartService.findByUserId(userLogged.getUserId());
	        for (Cart cart : list) {
	        	total += cart.getQuantity();
	        }
	        return total;
	    }
	    return 0;
	}
	
	@PostMapping("/add-rating")
	public String add(@RequestParam("nickname") String nickname,
	                  @RequestParam("content") String content,
	                  @RequestParam("platform") String platform,
	                  @RequestParam("rate") int rate,
	                  ModelMap model, HttpServletRequest request, HttpServletResponse response) {

	    Rating entity = new Rating();
	    entity.setNickname(nickname);
	    entity.setContent(content);
	    entity.setPlatform(platform);
	    entity.setRate(rate);
	    entity.setDisplay(0);

	    ratingService.save(entity);
	    
	    ResponseCookie cookie = ResponseCookie.from("JSESSIONID", request.getSession().getId()) // key & value
                .httpOnly(true)
                .secure(true)       
                .maxAge(Duration.ofHours(1))
                .sameSite("Strict")  // sameSite
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

	    return "redirect:/"; // Điều hướng đến trang chính hoặc trang khác tùy ý
	}

	@GetMapping("/undeveloped")
	public String undeloped() {
		return "undeveloped";
	}

}
