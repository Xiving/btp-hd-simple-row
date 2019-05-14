package btp.hd.cji.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class PgmReader {
    public static double[][] read(String fileName) {
        int height = 0;
        int width = 0;
        double[][] matrix;

        BufferedReader br = openBufferedReader(fileName);

        try { // read dimensions
            StringTokenizer dimensions = new StringTokenizer(br.readLine());
            height = Integer.parseInt(dimensions.nextToken());
            width = Integer.parseInt(dimensions.nextToken());
        } catch (IOException e) {
            System.err.println("Invalid integer found!");
            System.exit(1);
        }

        matrix = new double[height][width];

        try { // read values
            for (int i = 0; i < height; i++) {
                StringTokenizer row = new StringTokenizer(br.readLine());

                for (int j = 0; j < width; j++) {
                    matrix[i][j] = Double.parseDouble(row.nextToken());
                }
            }
        } catch (IOException e) {
            System.err.println("Invalid double found!");
            System.exit(1);
        }

        return matrix;
    }

    private static BufferedReader openBufferedReader(String fileName) {
        try {
            return new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open buffered reader!");
            System.exit(1);
        }

        return null; // ignore
    }

}
