package com.giaidev.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.giaidev")
public class IdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityApplication.class, args);
    }

}
///  Tôi đang làm dự án Wealth Ledger theo Structure Modular Monolith. Sử dụng postgresql thay mySQL thì tôi muốn hỏi là Config và Mapper build ở đâu trong core hay trong từng module
