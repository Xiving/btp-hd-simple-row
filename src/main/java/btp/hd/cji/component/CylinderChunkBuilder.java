package btp.hd.cji.component;

import btp.hd.cji.model.HeatChunkWithHalo;

public class CylinderChunkBuilder {

    public static HeatChunkWithHalo build(double[][] temp, double[][] cond) {
        int height = temp.length;
        int width = temp[0].length;

        double[][] tempChunk = new double[height + 2][width + 2];
        double[][] condChunk = new double[height+ 2][width + 2];

        for (int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                tempChunk[i + 1][j + 1] = temp[i][j];
                condChunk[i + 1][j + 1] = cond[i][j];
            }

            tempChunk[i][0] = temp[i][width - 1];
            condChunk[i][0] = cond[i][width - 1];

            tempChunk[i][width + 1] = temp[i][1];
            condChunk[i][width + 1] = cond[i][1];
        }

        return new HeatChunkWithHalo(tempChunk, condChunk);
    }
}
