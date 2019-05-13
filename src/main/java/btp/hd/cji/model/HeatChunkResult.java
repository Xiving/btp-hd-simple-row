package btp.hd.cji.model;

import lombok.Data;

@Data
public class HeatChunkResult {

    private final double[][] result;
    private final int offsetInParent;

    synchronized void add(HeatChunkResult chunk) {
        double[][] toAdd = chunk.getResult();

        for (int i = 0; i < toAdd.length; i++) {
            for(int j = 0; j < toAdd[0].length; j++) {
                result[i + chunk.getOffsetInParent()] = toAdd[i];
            }
        }
    }

}
