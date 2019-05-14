package btp.hd.cji.model;

import lombok.Data;

@Data
public class HeatChunkStep {

    private static final double DIRECT_CONST = 0.25 * Math.sqrt(2) / (Math.sqrt(2) + 1.0);
    private static final double DIAGONAL_CONST = 0.25 / (Math.sqrt(2) + 1.0);

    private final double[][] temp;
    private final double[][] cond;

    public HeatChunkStep(double[][] temp, double[][] cond) {
        if (temp.length != cond.length || temp[0].length != cond[0].length) {
            throw new Error(
                    "Input temp and cond matrices are required to have the same dimensions"
            );
        }

        this.temp = temp;
        this.cond = cond;
    }

    public HeatChunkResult result() {
        double maxDifference = 0;
        double[][] result = new double[temp.length - 2][temp[0].length - 2];

        for (int i = 1; i < temp.length - 1; i++) {
            for (int j = 1; j < temp[0].length - 1; j++) {
                result[i][j] = nextTemp(i, j);

                double difference = Math.abs(temp[i][j] - result[i][j]);

                if(difference > maxDifference) {
                    maxDifference = difference;
                }
            }
        }

        return new HeatChunkResult(maxDifference, result);
    }

    private double nextTemp(int i, int j) {
        double w = cond[i][j];
        double restW = 1 - w;

        return temp[i][j] * w +
                (temp[i][j - 1] + temp[i - 1][j] + temp[i + 1][j] + temp[j + 1][i]) * (restW * DIRECT_CONST) +
                (temp[i - 1][j - 1] + temp[i - 1][j + 1] + temp[i + 1][j - 1] + temp[j + 1][i + 1]) * (restW * DIAGONAL_CONST);
    }
}
