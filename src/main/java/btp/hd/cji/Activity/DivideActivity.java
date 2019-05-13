package btp.hd.cji.Activity;

import btp.hd.cji.model.HeatChunk;
import btp.hd.cji.model.HeatChunkResult;
import ibis.constellation.Activity;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Constellation;
import ibis.constellation.Context;
import ibis.constellation.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DivideActivity extends Activity {

    private static final boolean EXPECT_EVENTS = true;
    private static final String LABEL = "DIVIDE_ACTIVITY";

    private ActivityIdentifier parent;
    private int threshold;
    private HeatChunkResult result;

    public DivideActivity(ActivityIdentifier parent, HeatChunk chunk, int threshold, int height, int offset) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.threshold = threshold;

        this.result = new HeatChunkResult(new double[height][chunk.width()], offset);
    }

    @Override
    public int initialize(Constellation constellation) {
        return 0;
    }

    @Override
    public int process(Constellation constellation, Event event) {
        return 0;
    }

    @Override
    public void cleanup(Constellation constellation) {

    }
}
