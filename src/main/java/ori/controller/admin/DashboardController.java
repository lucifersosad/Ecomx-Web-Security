package ori.controller.admin;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import ori.service.IOrderService;

@Controller

@RequestMapping("admin")

public class DashboardController {
	@Autowired(required = true)
	IOrderService orderService;
	@RequestMapping({"", "/"})
	public String dashboard(ModelMap model) {
		
		Locale localeVietnam = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVietnam);
		
		model.addAttribute("reMonth",currencyFormatter.format(orderService.reOnCurrentMonth()));
		model.addAttribute("reYear",currencyFormatter.format(orderService.reOnCurrentYear()));
		model.addAttribute("reQuarter",currencyFormatter.format(orderService.reOnCurrentQuarter()));
		model.addAttribute("rateCom",orderService.rateCom());
		
		model.addAttribute("totalMontly", orderService.getMonthlyTotal());
		model.addAttribute("totalQuarter", orderService.getQuarterTotal());

		return "admin/dashboard";
	}	
}
