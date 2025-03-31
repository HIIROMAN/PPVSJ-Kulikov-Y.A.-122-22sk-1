package com.example.dniproapartmentfinder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private static final String PRIVATBANK_API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private static final String NBU_API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    private final RestTemplate restTemplate;

    public ExchangeRateService() {
        this.restTemplate = new RestTemplate();
    }

    public double getUsdRate() {
        // Спочатку пробуємо ПриватБанк
        Double rateFromPrivatBank = getUsdRateFromPrivatBank();
        if (rateFromPrivatBank != null) {
            return rateFromPrivatBank;
        }

        // Якщо ПриватБанк не повернув курс, пробуємо НБУ
        logger.info("Falling back to NBU API for exchange rate");
        Double rateFromNbu = getUsdRateFromNbu();
        if (rateFromNbu != null) {
            return rateFromNbu;
        }

        // Якщо обидва API не працюють, повертаємо резервний курс
        logger.warn("No exchange rate found from either PrivatBank or NBU API, using default rate 41.0");
        return 41.0;
    }

    private Double getUsdRateFromPrivatBank() {
        try {
            PrivatBankExchangeRate[] rates = restTemplate.getForObject(PRIVATBANK_API_URL, PrivatBankExchangeRate[].class);
            if (rates == null || rates.length == 0) {
                logger.error("No exchange rates received from PrivatBank API");
                return null;
            }

            logger.info("PrivatBank API response: {}", java.util.Arrays.toString(rates));

            for (PrivatBankExchangeRate rate : rates) {
                // Перевіряємо, чи валюта USD і чи base_ccy є UAH або null
                if ("USD".equalsIgnoreCase(rate.getCcy()) && (rate.getBaseCcy() == null || "UAH".equalsIgnoreCase(rate.getBaseCcy()))) {
                    double buyRate = Double.parseDouble(rate.getBuy());
                    double saleRate = Double.parseDouble(rate.getSale());
                    double averageRate = (buyRate + saleRate) / 2;
                    logger.info("USD exchange rate from PrivatBank: buy={}, sale={}, average={}", buyRate, saleRate, averageRate);
                    return averageRate;
                }
            }

            logger.warn("USD rate not found in PrivatBank API response");
            return null;
        } catch (Exception e) {
            logger.error("Error fetching exchange rate from PrivatBank API: {}", e.getMessage(), e);
            return null;
        }
    }

    private Double getUsdRateFromNbu() {
        try {
            NbuExchangeRate[] rates = restTemplate.getForObject(NBU_API_URL, NbuExchangeRate[].class);
            if (rates == null || rates.length == 0) {
                logger.error("No exchange rates received from NBU API");
                return null;
            }

            logger.info("NBU API response: {}", java.util.Arrays.toString(rates));

            for (NbuExchangeRate rate : rates) {
                if ("USD".equalsIgnoreCase(rate.getCc())) {
                    double rateValue = rate.getRate();
                    logger.info("USD exchange rate from NBU: {}", rateValue);
                    return rateValue;
                }
            }

            logger.warn("USD rate not found in NBU API response");
            return null;
        } catch (Exception e) {
            logger.error("Error fetching exchange rate from NBU API: {}", e.getMessage(), e);
            return null;
        }
    }
}

class PrivatBankExchangeRate {
    private String ccy;
    private String base_ccy;
    private String buy;
    private String sale;

    public String getCcy() { return ccy; }
    public void setCcy(String ccy) { this.ccy = ccy; }
    public String getBaseCcy() { return base_ccy; }
    public void setBaseCcy(String base_ccy) { this.base_ccy = base_ccy; }
    public String getBuy() { return buy; }
    public void setBuy(String buy) { this.buy = buy; }
    public String getSale() { return sale; }
    public void setSale(String sale) { this.sale = sale; }

    @Override
    public String toString() {
        return "PrivatBankExchangeRate{" +
                "ccy='" + ccy + '\'' +
                ", base_ccy='" + base_ccy + '\'' +
                ", buy='" + buy + '\'' +
                ", sale='" + sale + '\'' +
                '}';
    }
}

class NbuExchangeRate {
    private String cc;
    private double rate;

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    @Override
    public String toString() {
        return "NbuExchangeRate{" +
                "cc='" + cc + '\'' +
                ", rate=" + rate +
                '}';
    }
}