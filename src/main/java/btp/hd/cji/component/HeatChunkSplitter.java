package btp.hd.cji.component;

import btp.hd.cji.model.HeatChunkWithHalo;
import lombok.Data;

@Data
public class HeatChunkSplitter {

    private final HeatChunkWithHalo top;
    private final HeatChunkWithHalo bot;

    public HeatChunkSplitter(HeatChunkWithHalo chunk) {
        top = makeTop(chunk);
        bot = makeBot(chunk);
    }

    private static HeatChunkWithHalo makeTop(HeatChunkWithHalo chunk) {
        double half = ((double) chunk.haloHeight()) / 2;
        int topHeight = (int) (Math.ceil(half) + 1);

        double[][] temp = chunk.getTemp();
        double[][] cond = chunk.getCond();

        double[][] topTemp = new double[topHeight][chunk.haloWidth()];
        double[][] topCond = new double[topHeight][chunk.haloWidth()];

        for (int i = 0; i < topHeight; i++) {
            for (int j = 0; j < chunk.haloWidth(); j++) {
                topTemp[i][j] = temp[i][j];
                topCond[i][j] = cond[i][j];
            }
        }

        return new HeatChunkWithHalo(topTemp, topCond);
    }

    private static HeatChunkWithHalo makeBot(HeatChunkWithHalo chunk) {
        double half = ((double) chunk.haloWidth()) / 2;
        int topHeight = (int) (Math.ceil(half) + 1);
        int botHeight = (int) (Math.floor(half) + 1);
        int botOffset =  topHeight - 2;

        double[][] temp = chunk.getTemp();
        double[][] cond = chunk.getCond();

        double[][] botTemp = new double[botHeight][chunk.haloWidth()];
        double[][] botCond = new double[botHeight][chunk.haloWidth()];


        for (int i = 0; i < botHeight; i++) {
            for (int j = 0; j < chunk.haloWidth(); j++) {
                botTemp[i][j] = temp[i + botOffset][j];
                botCond[i][j] = cond[i + botOffset][j];
            }
        }

        return new HeatChunkWithHalo(botTemp, botCond, topHeight);
    }
}
