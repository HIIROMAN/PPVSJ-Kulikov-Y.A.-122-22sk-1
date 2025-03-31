package com.example.dniproapartmentfinder.controller;

import com.example.dniproapartmentfinder.model.Apartment;
import com.example.dniproapartmentfinder.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ApartmentService apartmentService;

    @GetMapping("/")
    public String home(Model model) {
        List<Apartment> apartments = apartmentService.getAllApartments();
        model.addAttribute("apartments", apartments);
        return "index"; // Повертаємо Thymeleaf-шаблон index.html
    }
}