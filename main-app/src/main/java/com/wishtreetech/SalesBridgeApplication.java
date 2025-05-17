package com.wishtreetech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wishtreetech")
public class SalesBridgeApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(SalesBridgeApplication.class, args);
    }
}
