package btp.hd.cji.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public abstract class AbstractHeatChunk extends AbstractTempChunk{

    private final double[][] cond;

    public AbstractHeatChunk(double[][] temp, double[][] cond) {
        super(temp, 0);

        if (temp.length != cond.length || temp[0].length != cond[0].length) {
            throw new IllegalArgumentException("Dimension of temp and cond have to match");
        }

        this.cond = cond;
    }

    public AbstractHeatChunk(double[][] temp, double[][] cond, int offset) {
        super(temp, offset);

        if (temp.length != cond.length || temp[0].length != cond[0].length) {
            throw new IllegalArgumentException("Dimension of temp and cond have to match");
        }

        this.cond = cond;
    }
}
