package com.zistone.gprs.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class MyPropertiesUtil {
    /**
     * （禁止外部实例化）
     */
    private MyPropertiesUtil() {
    }

    public static Properties GetValueProperties() {
        Properties properties = new Properties();
        InputStream inputStream = Object.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
