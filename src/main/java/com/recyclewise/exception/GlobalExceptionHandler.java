package com.recyclewise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Centralised exception handling for all controllers.
 *
 * SOLID — (S) Single Responsibility: one class owns ALL error-response rendering
 * SOLID — (O) Open/Closed: add new @ExceptionHandler methods to extend; never touch controllers
 * OOP   — Abstraction: controllers throw exceptions and are completely unaware of HTTP status codes
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 404 — item not found.
     */
    @ExceptionHandler({ResourceNotFoundException.class, 
                       org.springframework.web.servlet.resource.NoResourceFoundException.class,
                       org.springframework.web.servlet.NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Item Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("suggestion", "The item you're looking for doesn't exist. Try browsing the waste guide.");
        model.addAttribute("backUrl", "/guide");
        model.addAttribute("backLabel", "Back to Waste Guide");
        return "pages/error";
    }

    /**
     * Handles all other unexpected errors — 500.
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Throwable ex, Model model) {
        ex.printStackTrace(); // Keep logging the stack trace to console
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Something Went Wrong");
        model.addAttribute("errorMessage", ex.getClass().getSimpleName() + ": " + (ex.getMessage() != null ? ex.getMessage() : "Unknown Error"));
        model.addAttribute("suggestion", "Our team has been notified. Please try again later or go back to the home page.");
        model.addAttribute("backUrl", "/");
        model.addAttribute("backLabel", "Go Home");
        return "pages/error";
    }
}
