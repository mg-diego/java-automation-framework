package Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class CSVHelper {

    public static boolean compareCSVFiles(String filePath1, String filePath2) {
        try {
            List<String[]> file1Data = readCSV(filePath1);
            List<String[]> file2Data = readCSV(filePath2);
            return compareData(file1Data, file2Data);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            data.add(values);
        }

        reader.close();
        return data;
    }

    private static boolean compareData(List<String[]> file1Data, List<String[]> file2Data) {
        int maxRows = Math.max(file1Data.size(), file2Data.size());
        boolean areEqual = true;
        List<String> differences = new ArrayList<>();

        for (int i = 0; i < maxRows; i++) {
            String[] row1 = (i < file1Data.size()) ? file1Data.get(i) : new String[]{};
            String[] row2 = (i < file2Data.size()) ? file2Data.get(i) : new String[]{};

            int maxColumns = Math.max(row1.length, row2.length);

            for (int j = 0; j < maxColumns; j++) {
                String value1 = (j < row1.length) ? row1[j] : "";
                String value2 = (j < row2.length) ? row2[j] : "";

                if (!value1.equals(value2)) {
                    differences.add("Difference at row " + (i + 1) + ", column " + (j + 1) + ": '" + value1 + "' vs '" + value2 + "'");
                    areEqual = false;
                }
            }
        }

        for (String difference : differences) {
            System.out.println(difference);
        }

        return areEqual;
    }
}

