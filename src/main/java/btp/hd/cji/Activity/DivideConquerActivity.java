package btp.hd.cji.Activity;

import btp.hd.cji.model.HeatChunkWithHalo;
import btp.hd.cji.model.TempChunkResult;
import btp.hd.cji.service.HeatChunkSplitter;
import btp.hd.cji.service.TempChunkResultBuilder;
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
    private boolean splitChunk = false;

    public DivideConquerActivity(ActivityIdentifier parent, HeatChunkWithHalo chunk, int threshold) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.chunk = chunk;
        this.threshold = threshold;

        log.info("Created divide and conquer activity with chunk of size {} x {}", chunk.height(), chunk.width());
    }

    @Override
    public int initialize(Constellation cons) {
        if(chunk.height() <= threshold) {
            log.debug("Chunk with height {} is small enough to be calculated", chunk.height());

            try {
                cons.submit(new StencilOperationActivity(parent, chunk));
            } catch (NoSuitableExecutorException e) {
                e.printStackTrace();
            }

            return FINISH;
        } else {
            log.debug("Chunk with height {} is too big. Will be split into smaller chunks", chunk.height());
            resultBuilder = new TempChunkResultBuilder(chunk.height(), chunk.width(), chunk.getOffsetInParent());
            HeatChunkSplitter splitter = new HeatChunkSplitter(chunk);
            splitChunk = true;

            try {
                cons.submit(new DivideConquerActivity(identifier(), splitter.getTop(), threshold));
                cons.submit(new DivideConquerActivity(identifier(), splitter.getBot(), threshold));
            } catch (NoSuitableExecutorException e) {
                e.printStackTrace();
            }

            return SUSPEND;
        }
    }

    @Override
    public int process(Constellation cons, Event event) {
        if (log.isDebugEnabled()) {
            log.debug("Processing an event");
        }

        resultBuilder.add((TempChunkResult) event.getData());

        if (resultBuilder.finished()) {
            return FINISH;
        }

        return SUSPEND;
    }

    @Override
    public void cleanup(Constellation cons) {
        if(splitChunk) {
            log.debug("Sending an event to my parent");
            cons.send(new Event(identifier(), parent, resultBuilder.getResult()));
        }
    }
}
