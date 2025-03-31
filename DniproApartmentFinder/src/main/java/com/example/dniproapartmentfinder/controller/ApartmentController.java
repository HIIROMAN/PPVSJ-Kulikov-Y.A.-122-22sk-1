package com.example.dniproapartmentfinder.controller;

import com.example.dniproapartmentfinder.model.Apartment;
import com.example.dniproapartmentfinder.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    @GetMapping
    public List<Apartment> getAllApartments() {
        return apartmentService.getAllApartments();
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refreshData() {
        apartmentService.refreshApartmentData();
        // Перенаправлення на головну сторінку після оновлення
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/")
                .body("Redirecting to home page...");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        byte[] excelBytes = apartmentService.exportToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "apartments.xlsx");
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}