package btp.hd.cji.model;

import lombok.Data;

@Data
public abstract class  AbstractTempChunk implements java.io.Serializable {

    private final double[][] temp;
    private final int offsetInParent;

    public int height() {
        return temp.length;
    }

    public int width() {
        return temp[0].length;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (double[] row: temp) {
            for (double d: row) {
                str.append(String.format("%10.3f", d)).append(' ');
            }

            str.append('\n');
        }

        return str.toString();
    }
}
