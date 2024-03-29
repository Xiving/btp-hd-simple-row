package btp.hd.simple_row.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class TempResult extends TempChunk {

    private final int parentOffset;

    private double maxDifference;
    private int rowsAdded;

    private TempResult(double[][] temp, int parentOffset, double maxDifference) {
        super(temp);
        this.parentOffset = parentOffset;
        this.maxDifference = maxDifference;
        this.rowsAdded = temp.length;
    }

    private TempResult(int height, int width, int parentOffset) {
        super(new double[height][width]);
        this.parentOffset = parentOffset;
        this.maxDifference = 0;
        this.rowsAdded = 0;
    }

    public static TempResult of(double[][] temp, int offsetFromParent, double maxDifference) {
        return new TempResult(temp, offsetFromParent, maxDifference);
    }

    public static TempResult of(CylinderSlice slice) {
        return new TempResult(slice.height() - 2, slice.width() - 2, slice.getParentOffset());
    }

    public void add(TempResult result) {
        System.arraycopy(result.getTemp(), 0, getTemp(), result.getParentOffset(), result.height());
        rowsAdded += result.height();
        maxDifference = Math.max(maxDifference, result.getMaxDifference());
    }

    public boolean finished() {
        return rowsAdded >= getTemp().length;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
