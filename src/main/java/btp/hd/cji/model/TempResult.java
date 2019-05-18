package btp.hd.cji.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class TempResult extends TempChunk {

    private final int parentOffset;
    private final double maxDifference;

    private int rowsAdded;

    private TempResult(double[][] temp, int parentOffset, double maxDifference) {
        super(temp);
        this.parentOffset = parentOffset;
        this.maxDifference = maxDifference;
        this.rowsAdded = temp.length;
    }

    public TempResult(int height, int width, int parentOffset) {
        super(new double[height][width]);
        this.parentOffset = parentOffset;
        this.maxDifference = 0;
        this.rowsAdded = 0;
    }

    public static TempResult of(double[][] temp, int offsetFromParent, double maxDifference) {
        return new TempResult(temp, offsetFromParent, maxDifference);
    }

    public void add(TempResult result) {
        System.arraycopy(result.getTemp(), 0, getTemp(), result.getParentOffset(), result.height());
        rowsAdded += result.height();
    }

    public boolean finished() {
        log.info("Rows added {}", rowsAdded);
        return rowsAdded >= getTemp().length;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
