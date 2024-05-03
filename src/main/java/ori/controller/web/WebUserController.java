package ori.controller.web;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ori.config.scurity.AuthUser;
import ori.entity.Order;
import ori.entity.OrderDetail;
import ori.entity.Product;
import ori.entity.User;
import ori.model.UserModel;
import ori.service.IOrderDetailService;
import ori.service.IOrderService;
import ori.service.IUserService;

@Controller
@RequestMapping("web/users")
public class WebUserController {
	@Autowired(required=true)
	IUserService userService;
	
	@Autowired(required=true)
	IOrderDetailService orderDetailService;
	
	@Autowired(required = true)
	IOrderService orderService;
	
	@GetMapping("/my-account")
	public String myAccount(ModelMap model) {
		Object authen = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (authen instanceof AuthUser) {
			String email = ((AuthUser)authen).getEmail();
			Optional<User> optUser = userService.findByEmail(email);
			if (optUser.isPresent()) {
				User user = optUser.get();
				UserModel userModel = new UserModel();
	        	BeanUtils.copyProperties(user, userModel);
				userModel.setIsEdit(true);
				String add = userModel.getAddress();
				String[] parts = add.split("\\s*,\\s*");
				if (parts.length >= 3) {
				    model.addAttribute("city", parts[3].trim()); 
				    model.addAttribute("district", parts[2].trim()); 
				    model.addAttribute("town", parts[1].trim()); 
				    model.addAttribute("homeaddress", parts[0].trim()); 
				} else {

				    model.addAttribute("homeaddress", add.trim());
				}
				model.addAttribute("user", userModel);
				
				List<Order> listOrder = orderService.findOder(user.getUserId());
				Collections.sort(listOrder, Comparator.comparing(Order::getOrderId).reversed());
				model.addAttribute("listOrder", listOrder);
				
				List<OrderDetail> listOderDetail = orderDetailService.findAll();
				model.addAttribute("listOderDetail", listOderDetail);
				
				List<Product> listPro = orderDetailService.listProByOderID(user.getUserId());
				for (Product product : listPro) {
					float oldprice = product.getPrice();
					product.setPrice(Math.round(oldprice * (100 - product.getSale()) / 100));
				}
				model.addAttribute("listPro", listPro);
				return "web/users/infor";
			}	
		} 
		return "redirect:/";
	}
	
	@GetMapping("edit/{userId}")
	public ModelAndView edit(ModelMap model, @PathVariable("userId") Integer userId) {
		Optional<User> optUser = userService.findById(userId);
		UserModel userModel = new UserModel();
		if (optUser.isPresent()) {
			User entity = optUser.get();
			BeanUtils.copyProperties(entity, userModel);
			userModel.setIsEdit(true);
			model.addAttribute("user", userModel);
			return new ModelAndView("web/users/addOrEdit", model);
		}
		model.addAttribute("message", "User is not existed!!!!");
		return new ModelAndView("forward:/web/users/", model);

	}
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("user") UserModel userModel, BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("web/users/error");
		}
		User entity = new User();

		BeanUtils.copyProperties(userModel, entity);
		userService.updateUser(entity);

		String message = "";
		if (userModel.getIsEdit() == true) {
			message = "User is Edited!!!!!!!!";
		} else {
			message = "User is saved!!!!!!!!";
		}
		model.addAttribute("message", message);
//redirect về URL controller
		return new ModelAndView("/web/users/infor", model);

	}
	@GetMapping("/infor/{email}")
	public ModelAndView infor(ModelMap model, @PathVariable("email") String email) {
	    Optional<User> optUser = userService.findByEmail(email);

	    if (optUser.isPresent()) {
	        User user = optUser.get();
        	UserModel userModel = new UserModel();
        	BeanUtils.copyProperties(user, userModel);
			userModel.setIsEdit(true);
			String add = userModel.getAddress();
			String[] parts = add.split("\\s*,\\s*");
			if (parts.length >= 3) {
			    model.addAttribute("city", parts[3].trim()); 
			    model.addAttribute("district", parts[2].trim()); 
			    model.addAttribute("town", parts[1].trim()); 
			    model.addAttribute("homeaddress", parts[0].trim()); 
			} else {

			    model.addAttribute("homeaddress", add.trim());
			}
			model.addAttribute("user", userModel);
			
			List<Order> listOrder = orderService.findOder(user.getUserId());
			Collections.sort(listOrder, Comparator.comparing(Order::getOrderId).reversed());
			model.addAttribute("listOrder", listOrder);
			
			List<OrderDetail> listOderDetail = orderDetailService.findAll();
			model.addAttribute("listOderDetail", listOderDetail);
			
			List<Product> listPro = orderDetailService.listProByOderID(user.getUserId());
			model.addAttribute("listPro", listPro);
            return new ModelAndView("web/users/infor", model);
	       
	    }
	    model.addAttribute("message", "User is not existed!!!!");
	    return new ModelAndView("forward:/web/users/", model);
	}
	@PostMapping("/updateAddress/{email}")
	public ModelAndView updateAddress(ModelMap model,
	                                   @PathVariable("email") String email,
	                                   @RequestParam("city") String city,
	                                   @RequestParam("district") String district,
	                                   @RequestParam("town") String town,
	                                   @RequestParam("homeaddress") String homeadd) {
	    Optional<User> optUser = userService.findByEmail(email);

	    if (optUser.isPresent()) {
	        User user = optUser.get();
	        UserModel userModel = new UserModel();
        	BeanUtils.copyProperties(user, userModel);
        	userModel.setIsEdit(true);
	        String address = homeadd+ " , " +town + " , " + district + " , " + city;
	        user.setAddress(address);
	        userService.updateUser(user);
	        model.addAttribute("message", "Address is updated!!!");
	        model.addAttribute("user", userModel);
	        return new ModelAndView("redirect:/web/users/infor/" + email);
	    }
	    return new ModelAndView("forward:/web/users/", model);
	}
	
	@PostMapping("/updateAddress")
    public ResponseEntity<String> updateAddress(@RequestParam("email") String email,
    										   @RequestParam("fullName") String fullName,
    										   @RequestParam("phone") String phone,
                                               @RequestParam("city") String city,
                                               @RequestParam("district") String district,
                                               @RequestParam("town") String town,
                                               @RequestParam("homeaddress") String homeadd) {

        Optional<User> optUser = userService.findByEmail(email);

        if (optUser.isPresent()) {
            User user = optUser.get();
            String address = homeadd + " , " + town + " , " + district + " , " + city;
            user.setAddress(address);
            user.setFullName(fullName);
            user.setPhone(phone);
            userService.updateUser(user);
            return new ResponseEntity<>("Cập nhật thông tin thành công", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cập nhật thông tin không thành công", HttpStatus.NOT_FOUND);
        }
    }
	
	@GetMapping("/profile")
	public ModelAndView info(ModelMap model, HttpSession session) {
	    String userEmail = session.getAttribute("Email").toString();
	    if (userEmail != null) {
	        Optional<User> optUser = userService.findByEmail(userEmail.toString());

	        if (optUser.isPresent()) {
	            User user = optUser.get();
	            UserModel userModel = new UserModel();
	        	BeanUtils.copyProperties(user, userModel);
	            model.addAttribute("user", userModel);
	            return new ModelAndView("web/users/infor", model);
	        }
	    }

	    model.addAttribute("message", "User is not logged in!");
	    return new ModelAndView("forward:/admin/users", model);
	}
	
}