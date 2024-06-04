package com.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class EmailServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(EmailServiceApplication.class,args);

    }
}
