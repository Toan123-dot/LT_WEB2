package com.truongquoctoan.example01.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String storeEmail;
    private String storePhone;
    private String storeAddress;

    private Boolean emailNotifications;
    private Boolean orderNotifications;
    private Boolean promotionNotifications;

    private String currency;
    private String timezone;
    private String language;
    private Integer taxRate;

    private String themeColor;
    private Boolean darkMode;

    private Double freeShippingThreshold;
    private Double shippingFee;
}

