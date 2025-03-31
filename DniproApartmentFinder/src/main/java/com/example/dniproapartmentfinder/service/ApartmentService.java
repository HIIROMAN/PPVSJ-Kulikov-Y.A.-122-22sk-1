package com.example.dniproapartmentfinder.service;

import com.example.dniproapartmentfinder.model.Apartment;
import com.example.dniproapartmentfinder.parser.OlxParser;
import com.example.dniproapartmentfinder.repository.ApartmentRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ApartmentService {
    private static final Logger logger = LoggerFactory.getLogger(ApartmentService.class);

    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private OlxParser olxParser;
    @Autowired
    private ExchangeRateService exchangeRateService;

    public List<Apartment> getAllApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        logger.info("Retrieved {} apartments from database", apartments.size());
        return apartments;
    }

    public void refreshApartmentData() {
        List<Apartment> apartments = olxParser.parseApartments();
        logger.info("Parsed {} apartments from OLX", apartments.size());

        if (apartments.isEmpty()) {
            logger.warn("No apartments parsed from OLX. Check OlxParser.");
            return;
        }

        double usdRate = exchangeRateService.getUsdRate(); // Використовуємо оновлений ExchangeRateService

        for (Apartment apartment : apartments) {
            if (apartment.getPriceUah() != null) {
                apartment.setPriceUsd(apartment.getPriceUah() / usdRate);
            } else {
                apartment.setPriceUsd(0.0);
                logger.warn("Price (UAH) is null for apartment: {}", apartment.getTitle());
            }
        }

        apartmentRepository.deleteAll();
        logger.info("Cleared existing apartments from database");
        apartmentRepository.saveAll(apartments);
        logger.info("Saved {} apartments to database", apartments.size());
    }

    public byte[] exportToExcel() throws IOException {
        List<Apartment> apartments = apartmentRepository.findAll();
        logger.info("Exporting {} apartments to Excel", apartments.size());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Apartments");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Title");
        header.createCell(1).setCellValue("Price (UAH)");
        header.createCell(2).setCellValue("Price (USD)");
        header.createCell(3).setCellValue("Date");

        int rowNum = 1;
        for (Apartment apt : apartments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(apt.getTitle());
            row.createCell(1).setCellValue(apt.getPriceUah() != null ? apt.getPriceUah() : 0.0);
            row.createCell(2).setCellValue(apt.getPriceUsd() != null ? apt.getPriceUsd() : 0.0);
            row.createCell(3).setCellValue(apt.getDatePosted());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}