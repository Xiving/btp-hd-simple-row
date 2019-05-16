package btp.hd.cji.service;

import btp.hd.cji.model.HeatChunkWithHalo;
import ibis.constellation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DivideConquerActivity extends Activity {

    public static final String LABEL = "divideAndConquerActivity";

    private static final boolean EXPECT_EVENTS = true;

    private final ActivityIdentifier parent;
    private final HeatChunkWithHalo chunk;
    private final int threshold;

    private TempChunkResultBuilder resultBuilder;

    public DivideConquerActivity(ActivityIdentifier parent, HeatChunkWithHalo chunk, int threshold) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.chunk = chunk;
        this.threshold = threshold;
    }

    @Override
    public int initialize(Constellation cons) {
        if(chunk.height() <= threshold) {
            log.debug("Chunk with height {} is small enough to be calculated", chunk.height());

        } else {
            log.debug("Chunk with height {} is too big. Will be split into smaller chunks", chunk.height());
            HeatChunkSplitter splitter = new HeatChunkSplitter(chunk);

        }
    }

    @Override
    public int process(Constellation cons, Event event) {
        return 0;
    }

    @Override
    public void cleanup(Constellation cons) {

    }
}
