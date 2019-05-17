package btp.hd.cji.Activity;

import btp.hd.cji.model.TempChunkResult;
import btp.hd.cji.model.HeatChunkWithHalo;
import btp.hd.cji.component.StencilOperation;
import ibis.constellation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StencilOperationActivity extends Activity {

    public static final String LABEL = "stencilOperation";

    private static final boolean EXPECT_EVENTS = false;

    private final ActivityIdentifier parent;
    private final HeatChunkWithHalo chunk;

    private TempChunkResult result;

    public StencilOperationActivity(ActivityIdentifier parent, HeatChunkWithHalo chunk) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.chunk = chunk;
    }

    @Override
    public int initialize(Constellation cons) {
        String executor = cons.identifier().toString();
        Timer timer = cons.getTimer("java", executor, "stencil operation");
        int timing = timer.start();

        log.info("Performing a stencil operation of size {} x {}", chunk.height(), chunk.width());

        result = StencilOperation.perform(chunk);

        timer.stop(timing);

        log.info("Finished stencil operation in {} ms'", timer.totalTimeVal());

        // We are done, indicate that we are ready to cleanup
        return FINISH;
    }

    @Override
    public int process(Constellation cons, Event event) {
        return 0;
    }

    @Override
    public void cleanup(Constellation cons) {
        log.debug("Sending and event to my parent");
        cons.send(new Event(identifier(), parent, result));
    }
}
