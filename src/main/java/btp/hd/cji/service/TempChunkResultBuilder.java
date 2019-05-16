package btp.hd.cji.service;

import btp.hd.cji.model.TempChunkResult;

public class TempChunkResultBuilder {

    private final double[][] result;
    private final int offsetInParent;

    private int rowsAdded = 0;

    public TempChunkResultBuilder(int height, int width, int offsetInParent) {
        this.result = new double[height][width];
        this.offsetInParent = offsetInParent;
    }

    public synchronized void add(TempChunkResult chunk) {
        double[][] toAdd = chunk.getTemp();

        for (int i = 0; i < toAdd.length; i++) {
            for (int j = 0; j < toAdd[0].length; j++) {
                result[i + chunk.getOffsetInParent()] = toAdd[i];
            }
        }

        rowsAdded += chunk.height();
    }

    public synchronized TempChunkResult getResult() {
        return new TempChunkResult(result, offsetInParent);
    }

    public boolean finished() {
        return rowsAdded >= result.length;
    }

}
