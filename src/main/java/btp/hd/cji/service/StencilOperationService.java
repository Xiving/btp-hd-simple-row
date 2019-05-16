package btp.hd.cji.service;

import btp.hd.cji.model.HeatChunkWithHalo;
import btp.hd.cji.model.TempChunkResult;

public class StencilOperationService {

    private static final double DIRECT_CONST = 0.25 * Math.sqrt(2) / (Math.sqrt(2) + 1.0);
    private static final double DIAGONAL_CONST = 0.25 / (Math.sqrt(2) + 1.0);


    public static TempChunkResult execute(HeatChunkWithHalo chunk) {
        double maxDifference = 0;
        double[][] temp = chunk.getTemp();
        double[][] cond = chunk.getCond();

        double[][] result = new double[temp.length - 2][temp[0].length - 2];

        for (int i = 0; i < temp.length - 2; i++) {
            for (int j = 0; j < temp[0].length - 2; j++) {
                result[i][j] = nextTemp(temp, cond, i + 1, j + 1);

                double difference = Math.abs(temp[i + 1][j + 1] - result[i][j]);

                if(difference > maxDifference) {
                    maxDifference = difference;
                }
            }
        }

        return new TempChunkResult(result, maxDifference);
    }

    private static double nextTemp(double[][] temp, double[][] cond, int i, int j) {
        double w = cond[i][j];
        double restW = 1 - w;

        return temp[i][j] * w +
                (temp[i][j - 1] + temp[i - 1][j] + temp[i + 1][j] + temp[j + 1][i]) * (restW * DIRECT_CONST) +
                (temp[i - 1][j - 1] + temp[i - 1][j + 1] + temp[i + 1][j - 1] + temp[j + 1][i + 1]) * (restW * DIAGONAL_CONST);
    }
}
