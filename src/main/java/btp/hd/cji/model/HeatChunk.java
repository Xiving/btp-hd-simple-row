package btp.hd.cji.model;

import com.google.common.collect.Lists;
import java.util.List;

public class HeatChunk {

    private int offset;
    private double[][] temp;
    private double[][] cond;

    private HeatChunk(double[][] temp, double[][] cond, int offset) {
        this.offset = offset;
        this.temp = temp;
        this.cond = cond;
        exchangeOuterColumns();
    }

    public static HeatChunk of(double[][] temp, double[][] cond, int offset) {
        if (temp.length != cond.length || temp[0].length != cond[0].length) {
            throw new Error(
                "Input temp and cond matrices are required to have the same dimensions"
            );
        }

        double[][] tempStencil = new double[temp.length + 2][temp[0].length + 2];
        double[][] condStencil = new double[temp.length + 2][temp[0].length + 2];

        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                tempStencil[i + 1][j + 1] = temp[i][j];
                condStencil[i + 1][j + 1] = cond[i][j];
            }
        }

        return new HeatChunk(tempStencil, condStencil, offset);
    }

    private void exchangeOuterColumns() {
        for (int i = 0; i < temp.length; i++) {
            temp[i][0] = temp[i][temp[0].length - 2];
            temp[i][temp[0].length - 1] = temp[i][1];
        }
    }

    public List<HeatChunk> splitIntoTwo() {
        double half = ((double) height()) / 2;
        int topHeight = (int) (Math.ceil(half) + 1);
        int botHeight = (int) (Math.floor(half) + 1);
        int botOffset = topHeight - 2;

        double[][] topTemp = new double[topHeight][width()];
        double[][] topCond = new double[topHeight][width()];

        double[][] botTemp = new double[botHeight][width()];
        double[][] botCond = new double[botHeight][width()];

        for (int i = 0; i < topHeight; i++) {
            for (int j = 0; j < width(); j++) {
                topTemp[i][j] = temp[i][j];
                topCond[i][j] = cond[i][j];
            }
        }

        for (int i = 0; i < botHeight; i++) {
            for (int j = 0; j < width(); j++) {
                botTemp[i][j] = temp[i + botOffset][j];
                botCond[i][j] = cond[i + botOffset][j];
            }
        }

        HeatChunk topChunk = new HeatChunk(topTemp, topCond, 0);
        HeatChunk bottomChunk = new HeatChunk(botTemp, botCond, topHeight);

        return Lists.newArrayList(topChunk, bottomChunk);
    }

    public int width() {
        return temp[0].length;
    }

    public int height() {
        return temp.length;
    }
}
