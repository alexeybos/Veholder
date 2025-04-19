package org.skillsmart.veholder.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest, Model model) {
        // Получаем все атрибуты ошибки
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STATUS, ErrorAttributeOptions.Include.ERROR,
                        ErrorAttributeOptions.Include.MESSAGE)
        );

        // Извлекаем статус
        Integer status = (Integer) errorAttributes.get("status");
        String errorMessage = (String) errorAttributes.get("message");
         if (isApiRequest(request)) {
             return Map.of(
                     "error", errorAttributes.get("error"),
                     "status", status,
                     "message", errorMessage,
                     "timestamp", LocalDateTime.now()
             );
         }
        // Устанавливаем сообщение
        model.addAttribute("errorMsg", "Ошибка " + status + ": " + errorMessage);
        return "error"; // Шаблон error.html
    }

    // Проверка, является ли запрос API-запросом
    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }
}
