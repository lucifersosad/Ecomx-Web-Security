package ori.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handError(HttpServletRequest request, Model model) {
        //Lấy thông tin loi tu request
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Tao ID loi doc nhat hoac thong tin tham chieu
        String errorRef = UUID.randomUUID().toString();

        // Thong bao loi chung ma khong phai loi thuc su
        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());
            model.addAttribute("errorCode", statusCode);
            model.addAttribute("errorRef", errorRef);
            return "ErrorPage";
        }
        return "Error";
    }
}
