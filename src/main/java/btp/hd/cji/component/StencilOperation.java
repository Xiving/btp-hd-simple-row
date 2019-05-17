package btp.hd.cji.component;

import btp.hd.cji.model.HeatChunkWithHalo;
import btp.hd.cji.model.TempChunkResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StencilOperation {

    private static final double DIRECT_CONST = 0.25 * Math.sqrt(2) / (Math.sqrt(2) + 1.0);
    private static final double DIAGONAL_CONST = 0.25 / (Math.sqrt(2) + 1.0);


    public static TempChunkResult perform(HeatChunkWithHalo chunk) {
        log.info("Performing stencil operation on chunk:\n{}", chunk.toString());

        double maxDifference = 0;
        int height = chunk.height();
        int width = chunk.width();
        double[][] temp = chunk.getTemp();
        double[][] cond = chunk.getCond();

        double[][] result = new double[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = nextTemp(temp, cond, i + 1, j + 1);

                double difference = Math.abs(temp[i + 1][j + 1] - result[i][j]);

                if (difference > maxDifference) {
                    maxDifference = difference;
                }
            }
        }

        TempChunkResult resultChunk = new TempChunkResult(result, maxDifference);
        log.info("Result of stencil operation on chunk:\n{}", resultChunk.toString());


    }

    private static double nextTemp(double[][] temp, double[][] cond, int i, int j) {
        double w = cond[i][j];
        double restW = 1 - w;

        return temp[i][j] * w +
            (temp[i - 1][j] + temp[i][j - 1] + temp[i][j + 1] + temp[i + 1][j]) * (restW
                * DIRECT_CONST) +
            (temp[i - 1][j - 1] + temp[i - 1][j + 1] + temp[i + 1][j - 1] + temp[i + 1][j + 1]) * (
                restW * DIAGONAL_CONST);
    }
}
