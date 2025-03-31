package com.example.dniproapartmentfinder;

import com.example.dniproapartmentfinder.service.ApartmentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ApartmentFinderApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ApartmentFinderApplication.class, args);
        ApartmentService apartmentService = context.getBean(ApartmentService.class);
        apartmentService.refreshApartmentData(); // Автоматичне оновлення даних при запуску
    }
}