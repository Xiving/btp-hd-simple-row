//package btp.hd.cji.Activity;
//
//import btp.hd.cji.model.HeatRow;
//import btp.hd.cji.model.HeatRowResult;
//import ibis.constellation.*;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.List;
//
//@Slf4j
//public class HeatRowActivity extends Activity {
//
//    private static final boolean EXPECT_EVENTS = true;
//    public static final String LABEL = "DIVIDE_ACTIVITY";
//
//    private ActivityIdentifier parent;
//    private int threshold;
//    private HeatRowResult result;
//    private HeatRow row;
//
//    private int nrReceivedEvents;
//
//    public HeatRowActivity(ActivityIdentifier parent, HeatRow row, int threshold, int offset) {
//        super(new Context(LABEL), EXPECT_EVENTS);
//
//        this.parent = parent;
//        this.threshold = threshold;
//
//        this.result = new HeatRowResult(new double[row.height()][row.width()], offset);
//        this.row = row;
//
//        this.nrReceivedEvents = 0;
//
//        if (log.isDebugEnabled()) {
//            log.debug("Initialized with {} x {} elements", row.height(), row.width());
//        }
//    }
//
//    @Override
//    public int initialize(Constellation cons) {
//
//        int height = row.height();
//
//        if (height <= threshold) {
//            String executor = cons.identifier().toString();
//            Timer timer = cons.getTimer("java", executor, "vector add");
//            int timing = timer.start();
//
//            log.debug("Compute a stencil of size {} x {}", row.height(), row.width());
//
//            row.compute();
//
//            timer.stop(timing);
//
//            return FINISH;
//        } else {
//            submit(cons, row.splitIntoTwo());
//
//            return SUSPEND;
//        }
//    }
//
//    private void submit(Constellation cons, List<HeatRow> rows) {
//        for (HeatRow row: rows) {
//            try {
//                cons.submit(new HeatRowActivity(identifier(), row, threshold, row.offset()))
//            } catch (NoSuitableExecutorException e) {
//                log.error("Submitting HeatRowActivity: {}", e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    public int process(Constellation constellation, Event event) {
//        return 0;
//    }
//
//    @Override
//    public void cleanup(Constellation constellation) {
//
//    }
//}
