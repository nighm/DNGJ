package com.assetmanagement.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TestDataReader {
    private static final Logger logger = LoggerFactory.getLogger(TestDataReader.class);
    private static final String TEST_DATA_DIR = "src/main/resources/testdata/";

    public static List<Map<String, String>> readCsvData(String filename) {
        List<Map<String, String>> data = new ArrayList<>();
        String filePath = TEST_DATA_DIR + filename;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = null;

            // 读取表头
            if ((line = br.readLine()) != null) {
                headers = line.split(",");
            }

            // 读取数据行
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    if (i < values.length) {
                        row.put(headers[i].trim(), values[i].trim());
                    } else {
                        row.put(headers[i].trim(), "");
                    }
                }

                data.add(row);
            }

            logger.info("Successfully read {} records from {}", data.size(), filename);
        } catch (IOException e) {
            logger.error("Error reading test data from {}", filename, e);
            throw new RuntimeException("Failed to read test data", e);
        }

        return data;
    }

    public static List<Map<String, String>> getLoginTestData() {
        return readCsvData("users.csv");
    }
} 