package ori.controller.admin;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import jakarta.validation.Valid;
import ori.entity.Rating;
import ori.model.RatingModel;
import ori.service.IRatingService;

@Controller

@RequestMapping("admin/ratings")

public class RatingController {
	@Autowired(required = true)
	IRatingService ratingService;
	
	@RequestMapping("")
	public String list(ModelMap model) {
		List<Rating> list = ratingService.findAll();
		Collections.reverse(list);
		model.addAttribute("ratings", list);
		return "admin/ratings/list";
	}

	@GetMapping("delete/{ratingId}")
	public ModelAndView delet(ModelMap model, @PathVariable("ratingId") Integer ratingId) {
		ratingService.deleteById(ratingId);
		model.addAttribute("message", "Rating is deleted!!!!");
		return new ModelAndView("redirect:/admin/ratings", model);
	}
	
	@GetMapping("updateState/{ratingId}/{newState}")
	public String updateOrderState(@PathVariable("ratingId") Integer ratingId, @PathVariable("newState") int newState) {
	    ratingService.updateRatingState(ratingId, newState);
	    return "redirect:/admin/ratings";
	}

}
