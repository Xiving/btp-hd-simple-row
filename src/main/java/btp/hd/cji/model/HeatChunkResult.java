package btp.hd.cji.model;

import lombok.Data;

@Data
public class HeatChunkResult {

    private final double maxDifference;
    private final double[][] temp;

}
