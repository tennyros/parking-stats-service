package com.github.tennyros.parkings;

import com.github.tennyros.parkings.util.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParkingApplication {

    public static void main(String[] args) {
        DotenvLoader.load();
        SpringApplication.run(ParkingApplication.class, args);
    }
}
