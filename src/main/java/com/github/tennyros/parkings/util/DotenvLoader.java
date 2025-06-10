package com.github.tennyros.parkings.util;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class DotenvLoader {

    public static void load() {
        Dotenv dotenv = Dotenv.load();

        // APP
        System.setProperty("APP_PORT", Objects.requireNonNull(dotenv.get("APP_PORT")));
        System.setProperty("APP_EPORT", Objects.requireNonNull(dotenv.get("APP_EPORT")));

        // DB
        System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USERNAME")));
        System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("DB_NAME", Objects.requireNonNull(dotenv.get("DB_NAME")));
        System.setProperty("DB_HOST", Objects.requireNonNull(dotenv.get("DB_HOST")));
        System.setProperty("DB_EPORT", Objects.requireNonNull(dotenv.get("DB_EPORT")));
    }
}
