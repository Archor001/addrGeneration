package com.hust.addrgeneration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

@MapperScan("com.hust.addrgeneration.dao")
@SpringBootApplication
public class AddrgenerationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AddrgenerationApplication.class, args);
    }

}
