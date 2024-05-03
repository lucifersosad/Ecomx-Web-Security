package ori.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import jakarta.validation.Valid;

import ori.entity.Order;
import ori.model.OrderModel;
import ori.service.IOrderService;

@Controller

@RequestMapping("admin/orders")
public class OrderController {
	@Autowired(required = true)
	IOrderService orderSer;
	@RequestMapping("")
	public String list(ModelMap model, @RequestParam(name="pageNo", defaultValue = "1") Integer pageNo) {
		Page<Order> list = orderSer.getAll(pageNo);
		model.addAttribute("orders", list);
		model.addAttribute("totalPage",list.getTotalPages());
		model.addAttribute("currentPage",pageNo);
		return "admin/orders/list.html";
	}
	
	@GetMapping("updateState/{orderId}/{newState}")
	public String updateOrderState(@PathVariable("orderId") Integer orderId, @PathVariable("newState") int newState) {
	    orderSer.updateOrderState(orderId, newState);
	    return "redirect:/admin/orders";
	}
	

	@GetMapping("delete/{orderId}")
	public ModelAndView delet(ModelMap model, @PathVariable("orderId") Integer orderId) {
		try {
			orderSer.deleteById(orderId);
			model.addAttribute("message", "Order is deleted!!!!");
		} catch (Exception e) {
			model.addAttribute("message", "Cannot delete!!!!");
		}
		return new ModelAndView("forward:/admin/orders", model);
	}
	
	
}
