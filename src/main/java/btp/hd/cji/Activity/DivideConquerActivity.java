package btp.hd.cji.Activity;

import btp.hd.cji.model.CylinderSlice;
import btp.hd.cji.model.TempResult;
import ibis.constellation.Activity;
import ibis.constellation.ActivityIdentifier;
import ibis.constellation.Constellation;
import ibis.constellation.Context;
import ibis.constellation.Event;
import ibis.constellation.NoSuitableExecutorException;
import ibis.constellation.Timer;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DivideConquerActivity extends Activity {

    public static final String LABEL = "divideAndConquer";

    private static final boolean EXPECT_EVENTS = true;

    private final ActivityIdentifier parent;
    private final CylinderSlice slice;
    private final int threshold;

    private TempResult result;
    private Timer timer;
    private int timerId;

    public DivideConquerActivity(ActivityIdentifier parent, CylinderSlice slice, int threshold) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.slice = slice;
        this.threshold = threshold;

        log.info("Created '{}' activity with size {} x {}", LABEL, slice.height(), slice.width());
    }

    @Override
    public int initialize(Constellation cons) {
        if (slice.height() <= threshold) {
            log.debug("Slice with height {} is small enough to be calculated", slice.height());
            submit(cons, slice);
            return FINISH;
        } else {
            log.debug("Slice with height {} is too big. Will be split into smaller slices",
                slice.height());

            result = TempResult.of(slice);

            String executor = cons.identifier().toString();
            timer = cons.getTimer("java", executor, "stencil operation");
            timerId = timer.start();

            int half = (int) Math.ceil((double) slice.height() / 2);
            submit(cons, slice, 0, half + 1);
            submit(cons, slice, half - 1, slice.height());

            return SUSPEND;
        }
    }

    private void submit(Constellation cons, CylinderSlice slice, int begin, int end) {
        CylinderSlice sliceToSubmit = CylinderSlice.of(slice, begin, end);

        try {
            cons.submit(new DivideConquerActivity(identifier(), sliceToSubmit, threshold));
        } catch (NoSuitableExecutorException e) {
            e.printStackTrace();
        }
    }

    private void submit(Constellation cons, CylinderSlice slice) {
        try {
            cons.submit(new StencilOperationActivity(parent, slice));
        } catch (NoSuitableExecutorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int process(Constellation cons, Event event) {
        if (log.isDebugEnabled()) {
            log.debug("Processing an event");
        }

        log.debug("Adding chunk to result");
        result.add((TempResult) event.getData());

        if (result.finished()) {
            timer.stop(timerId);
            log.debug("Performed  a stencil operation of size {} x {} in {} ms",
                slice.height(), slice.width(), timer.totalTimeVal() / 1000);
            return FINISH;
        }

        return SUSPEND;
    }

    @Override
    public void cleanup(Constellation cons) {
        if (Objects.nonNull(result)) {
            log.debug("Sending result to my parent");
            cons.send(new Event(identifier(), parent, result));
        }
    }
}
