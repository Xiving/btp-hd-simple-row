package btp.hd.cji.component;

import btp.hd.cji.model.TempChunkResult;

public class ResultBuilder {

    private final double[][] result;
    private final int offsetInParent;

    private int rowsAdded = 0;
    private double maxDifference = 0;

    public ResultBuilder(int height, int width, int offsetInParent) {
        this.result = new double[height][width];
        this.offsetInParent = offsetInParent;
    }

    public synchronized void add(TempChunkResult chunk) {
        double difference = chunk.getMaxDifference();
        double[][] toAdd = chunk.getTemp();

        for (int i = 0; i < toAdd.length; i++) {
            for (int j = 0; j < toAdd[0].length; j++) {
                result[i + chunk.getOffsetInParent()] = toAdd[i];
            }
        }

        rowsAdded += chunk.height();
        maxDifference = Math.max(maxDifference, difference);
    }

    public synchronized TempChunkResult getResult() {
        return new TempChunkResult(result, maxDifference, offsetInParent);
    }

    public boolean finished() {
        return rowsAdded >= result.length;
    }

}
