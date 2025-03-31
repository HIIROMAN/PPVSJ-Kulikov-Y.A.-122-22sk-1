package com.example.dniproapartmentfinder.parser;

import com.example.dniproapartmentfinder.model.Apartment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OlxParser {
    private static final Logger logger = LoggerFactory.getLogger(OlxParser.class);

    public List<Apartment> parseApartments() {
        List<Apartment> apartments = new ArrayList<>();
        try {
            // Додаємо userAgent і таймаут для уникнення блокування
            Document doc = Jsoup.connect("https://www.olx.ua/uk/nedvizhimost/kvartiry/dnepr/?currency=UAH")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000) // Таймаут 10 секунд
                    .get();

            // Логування HTML для діагностики (закоментуйте після перевірки)
            // logger.debug("HTML content: {}", doc.html());

            // Оновлений селектор для контейнерів оголошень
            Elements listings = doc.select("div.css-1g5933j");
            logger.info("Found {} listings", listings.size());

            if (listings.isEmpty()) {
                logger.warn("No listings found. Check if the selector 'div.css-1g5933j' is correct or if the page structure has changed.");
            }

            for (Element listing : listings) {
                Apartment apt = new Apartment();

                // Заголовок
                Element titleElement = listing.selectFirst("h4.css-1g61gc2");
                if (titleElement != null) {
                    apt.setTitle(titleElement.text());
                } else {
                    apt.setTitle("No title");
                    logger.warn("Title not found for listing: {}", listing.html());
                }

                // URL
                Element linkElement = listing.selectFirst("a.css-1tqlkj0");
                if (linkElement != null) {
                    apt.setUrl("https://www.olx.ua" + linkElement.attr("href"));
                } else {
                    apt.setUrl("");
                    logger.warn("URL not found for listing: {}", listing.html());
                }

                // Ціна
                Element priceElement = listing.selectFirst("p[data-testid=ad-price].css-uj7mm0");
                if (priceElement != null) {
                    String priceText = priceElement.text().replaceAll("[^0-9]", "");
                    try {
                        apt.setPriceUah(Double.parseDouble(priceText));
                    } catch (NumberFormatException e) {
                        apt.setPriceUah(0.0);
                        logger.error("Failed to parse price: {}", priceText, e);
                    }
                } else {
                    apt.setPriceUah(0.0);
                    logger.warn("Price not found for listing: {}", listing.html());
                }

                // Дата
                Element dateElement = listing.selectFirst("p[data-testid=location-date].css-vbz67q");
                if (dateElement != null) {
                    apt.setDatePosted(dateElement.text());
                } else {
                    apt.setDatePosted("No date");
                    logger.warn("Date not found for listing: {}", listing.html());
                }

                apartments.add(apt);
                logger.debug("Parsed apartment: {}", apt.getTitle());
            }
        } catch (IOException e) {
            logger.error("Error while parsing OLX: {}", e.getMessage(), e);
        }
        return apartments;
    }
}