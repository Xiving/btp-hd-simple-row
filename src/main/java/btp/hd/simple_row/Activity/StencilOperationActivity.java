package btp.hd.simple_row.Activity;

import btp.hd.simple_row.model.CylinderSlice;
import btp.hd.simple_row.model.TempResult;
import ibis.constellation.Activity;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Constellation;
import ibis.constellation.Context;
import ibis.constellation.Event;
import ibis.constellation.Timer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StencilOperationActivity extends Activity {

    public static final String LABEL = "stencilOperation";

    private static final boolean EXPECT_EVENTS = false;

    private final ActivityIdentifier parent;
    private final CylinderSlice slice;

    private TempResult result;

    public StencilOperationActivity(ActivityIdentifier parent, CylinderSlice slice) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.slice = slice;

        log.info("Created '{}' activity with size {} x {}", LABEL, slice.height() - 2, slice.width() - 2);
    }

    @Override
    public int initialize(Constellation cons) {
        String executor = cons.identifier().toString();
        Timer timer = cons.getTimer("java", executor, "stencil operation");
        int timing = timer.start();

        //log.debug("Performing stencil operation on:\n{}", slice.toString());
        result = slice.result();
        //log.debug("Result of stencil operation:\n{}", result.toString());

        timer.stop(timing);

        log.info("Performed  a stencil operation of size {} x {} in {} ms",
            slice.height(), slice.width(), timer.totalTimeVal() / 1000);

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
