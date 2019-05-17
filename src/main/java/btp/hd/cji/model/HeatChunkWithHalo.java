package btp.hd.cji.model;

import lombok.Data;

@Data
public class HeatChunkWithHalo extends AbstractHeatChunk {

    public HeatChunkWithHalo(double[][] temp, double[][] cond) {
        super(temp, cond);
    }

    public HeatChunkWithHalo(double[][] temp, double[][] cond, int offset) {
        super(temp, cond, offset);
    }

    @Override
    public int height() {
        return super.height() - 2;
    }

    @Override
    public int width() {
        return super.width() - 2;
    }

}
