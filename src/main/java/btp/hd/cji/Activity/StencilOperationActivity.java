package btp.hd.cji.Activity;

import btp.hd.cji.model.HeatChunkStep;
import btp.hd.cji.model.HeatChunkResult;
import ibis.constellation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StencilOperationActivity extends Activity {

    public static final String LABEL = "stencilOperation";

    private static final boolean EXPECT_EVENTS = false;

    private final ActivityIdentifier parent;
    private final double[][] temp;
    private final double[][] cond;

    private HeatChunkResult result;

    StencilOperationActivity(ActivityIdentifier parent, double[][] temp, double[][] cond) {
        super(new Context(LABEL), EXPECT_EVENTS);

        this.parent = parent;
        this.temp = temp;
        this.cond = cond;
    }

    @Override
    public int initialize(Constellation cons) {

            String executor = cons.identifier().toString();
            Timer timer = cons.getTimer("java", executor, "stencil operation");
            int timing = timer.start();

            log.debug("Perform stencil operation of size {} x {}", temp.length, temp[0].length);

            result = new HeatChunkStep(temp, cond).result();

            timer.stop(timing);

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
