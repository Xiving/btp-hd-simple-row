package btp.hd.cji.model;

import lombok.Data;

@Data
public abstract class AbstractTempChunk implements java.io.Serializable {

    private final double[][] temp;
    private final int offsetInParent;

    public int height() {
        return temp.length;
    }

    public int width() {
        return temp[0].length;
    }
}
