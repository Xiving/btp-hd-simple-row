package btp.hd.simple_row;

import btp.hd.simple_row.Activity.DivideConquerActivity;
import btp.hd.simple_row.Activity.StencilOperationActivity;
import btp.hd.simple_row.model.Cylinder;
import btp.hd.simple_row.model.CylinderSlice;
import btp.hd.simple_row.model.TempChunk;
import btp.hd.simple_row.model.TempResult;
import btp.hd.simple_row.util.HeatValueGenerator;
import btp.hd.simple_row.util.JobSubmission;
import btp.hd.simple_row.util.PgmReader;
import ibis.constellation.*;
import ibis.constellation.util.SingleEventCollector;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeatDissipatorApp {

    public static void writeFile(int it, double min, int w, int h, double ms, TempChunk temp, int nodes, int executors) {
        try {
            PrintStream out = new PrintStream(
                new FileOutputStream(String.format("heat-dissipator-n%d-e%d.out", nodes,executors), true)
            );

            out.println("Performed simple row heat dissipator sim");
            out.println(String.format("Iterations: %d, min temp delta: %f", it, min));
            out.println(String.format("Dimensions: %d x %d, time: %f ms\n", h, w, ms));
            out.close();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {

        // Default config
        String fileDir = null;
        int nrExecutorsPerNode = 1;
        double minDifference = 0.1;
        int maxIterations = Integer.MAX_VALUE;
        int height = 0;
        int width = 0;

        // overwrite defaults with input arguments
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-f":
                    fileDir = args[i + 1];
                    break;
                case "-e":
                    nrExecutorsPerNode = Integer.parseInt(args[i + 1]);
                    break;
                case "-d":
                    minDifference = Double.parseDouble(args[i + 1]);
                    break;
                case "-m":
                    maxIterations = Integer.parseInt(args[i + 1]);
                    break;
                case "-h":
                    height = Integer.parseInt(args[i + 1]);
                    break;
                case "-w":
                    width = Integer.parseInt(args[i + 1]);
                    break;
                default:
                    throw new Error("Usage: java HeatDissipatorApp "
                        + " -f fileDir "
                        + "[ -e <nrOfExecutors> ]"
                        + "[ -d <minDelta> ]"
                        + "[ -m <maxIteration> ]"
                        + "[ -h <height> ]"
                        + "[ -w <width> ]");
            }
        }

        if (Objects.isNull(fileDir) || height < 1 || width < 1) {
            throw new Error("Usage: java HeatDissipatorApp "
                + " -f fileDir "
                + "[ -e <nrOfExecutors> ]"
                + "[ -d <minDelta> ]"
                + "[ -m <maxIteration> ]"
                + "[ -h <height> ]"
                + "[ -w <width> ]");
        }

        int divideConquerThreshold = calcThreshold(nrExecutorsPerNode, height);

        OrContext orContext = new OrContext(new Context(StencilOperationActivity.LABEL), new Context(DivideConquerActivity.LABEL));

        // Initialize Constellation with the following configurations
        ConstellationConfiguration config =
            new ConstellationConfiguration(orContext,
                StealStrategy.SMALLEST, StealStrategy.BIGGEST,
                StealStrategy.BIGGEST);


        Constellation constellation =
            ConstellationFactory.createConstellation(config , nrExecutorsPerNode);

        constellation.activate();

        if (constellation.isMaster()) {
            log.info("HeatDissipatorApp, running with dimensions {} x {}:", height, width);
            // This is master specific code.  The rest is going to call
            // Constellation.done(), waiting for Activities to steal.

            HeatValueGenerator heatValueGenerator = new HeatValueGenerator(height, width, 0.05, 100);

            double[][] temp = PgmReader.getTempValues(fileDir, height, width);
            double[][] cond = PgmReader.getCondValues(fileDir, height, width);

            TempResult result = TempResult.of(temp, 0, 0);

            Timer overallTimer = constellation.getOverallTimer();
            int timing = overallTimer.start();

            //log.debug("Performing stencil operations on:\n{}", result.toString());

            int i = 0;
            do {

                SingleEventCollector sec = new SingleEventCollector(
                    new Context(DivideConquerActivity.LABEL));
                ActivityIdentifier aid = constellation.submit(sec);

                CylinderSlice slice = Cylinder.of(temp, cond).toSlice();
                constellation.submit(new DivideConquerActivity(aid, slice, divideConquerThreshold));

                log.debug(
                    "main(), just submitted, about to waitForEvent() for any event with target "
                        + aid);
                result = (TempResult) sec.waitForEvent().getData();
                log.debug("main(), done with waitForEvent() on identifier " + aid);

                log.debug("Performed stencil operation with max temperature delta {}",
                    result.getMaxDifference());

                temp = result.getTemp();
                i++;
                //log.debug("Iteration {}:\n{}", i, result.toString());
            } while (result.getMaxDifference() > minDifference && i < maxIterations);

            overallTimer.stop(timing);
            List<String> nodes = JobSubmission.getNodes();

            log.info("Result for iteration(s) {} calculated after {} ms", i, overallTimer.totalTimeVal() / 1000);
            writeFile(i, minDifference, width, height, overallTimer.totalTimeVal() /1000, result, nodes.size(), nrExecutorsPerNode);
        }
        log.debug("calling Constellation.done()");
        constellation.done();
        log.debug("called Constellation.done()");
    }

    private static int calcThreshold(int nrExecutorsPerNode, int height) {
        int pieces = 1;
        int pieceHeight = height;

        while (pieces < nrExecutorsPerNode) {
            pieceHeight = (int) Math.ceil((double) pieceHeight / 2);
            pieces *= 2;
        }

        return pieceHeight + 2;
    }

}
