package com.example.demo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidScheduleException.class)
    public String handleInvalidSchedule(
            InvalidScheduleException ex,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/feed";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(
            ResourceNotFoundException ex,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/feed";
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public String handleUnauthorized(
            UnauthorizedActionException ex,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/feed";
    }

    @ExceptionHandler(CustomException.class)
    public String handleCustom(
            CustomException ex,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/feed";
    }
}