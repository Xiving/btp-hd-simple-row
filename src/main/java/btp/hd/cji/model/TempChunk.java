package btp.hd.cji.model;

import lombok.Data;

@Data
public abstract class TempChunk implements java.io.Serializable {

    private final double[][] temp;

    public int height() {
        return temp.length;
    }

    public int width() {
        return temp[0].length;
    }
}
