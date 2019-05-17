package btp.hd.cji.model;

import lombok.Data;

@Data
public class TempChunkResult extends AbstractTempChunk {

    private final double maxDifference;

    public TempChunkResult(double[][] temp, double maxDifference, int offset) {
        super(temp, offset);
        this.maxDifference = maxDifference;
    }

    public TempChunkResult(double[][] temp, double maxDifference) {
        super(temp, 0);
        this.maxDifference = maxDifference;
    }
}
