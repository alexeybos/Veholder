package org.skillsmart.veholder.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Получаем статус ошибки
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMsg = "";

        if (statusCode != null) {
            switch (statusCode) {
                case 401:
                    errorMsg = "Ошибка 401: Требуется авторизация";
                    break;
                case 403:
                    errorMsg = "Ошибка 403: Доступ запрещён";
                    break;
                case 404:
                    errorMsg = "Ошибка 404: Страница не найдена";
                    break;
                default:
                    errorMsg = "Ошибка " + statusCode;
            }
        } else {
            errorMsg = "Неизвестная ошибка";
        }

        model.addAttribute("errorMsg", errorMsg);
        return "error"; // Имя Thymeleaf-шаблона (error.html)
    }
}
