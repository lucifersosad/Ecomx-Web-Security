package ori.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class NotFoundUrl implements ErrorController {
	@RequestMapping("/error")
    public String handleError() {
        return "404";
    }

    public String getErrorPath(Model model) {
        return "redirect:/error";
    }
}
