package btp.hd.cji;

import btp.hd.cji.Activity.StencilOperationActivity;
import btp.hd.cji.model.TempChunkResult;
import btp.hd.cji.model.HeatRow;
import btp.hd.cji.model.HeatChunkWithHalo;
import btp.hd.cji.Activity.DivideConquerActivity;
import btp.hd.cji.util.HeatValueGenerator;
import ibis.constellation.*;
import ibis.constellation.util.SingleEventCollector;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.PrintStream;

@Slf4j
public class HeatDissipator {

    public static void writeFile(double[][] temp) {
        try {
            PrintStream out = new PrintStream("heat-dissipator.out");

            for (int i = 0; i < temp.length; i++) {
                for (int j = 0; j < temp[0].length; j++) {
                    out.print(temp[i][j] + " ");
                }

                out.println();
            }
            out.close();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        // this code is executed on every node

        int divideConquerThreshold = 5;

        // the number of executors per node in the cluster
        int nrExecutorsPerNode = 4;

        // the threshold to decide whether to compute or divide tasks
        double minDifference = 10;

        int height = 10;
        int width = 10;

        // number of nodes in the cluster
        int nrNodes = 1;

        // determine the number of tasks based on the size of the pool of nodes
        String ibisPoolSize = System.getProperty("ibis.pool.size");
        if (ibisPoolSize != null) {
            nrNodes = Integer.parseInt(ibisPoolSize);
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-nrExecutorsPerNode")) {
                i++;
                nrExecutorsPerNode = Integer.parseInt(args[i]);
            } else if (args[i].equals("-minDifference")) {
                i++;
                minDifference = Double.parseDouble(args[i]);
            } else if (args[i].equals("-h")) {
                i++;
                height = Integer.parseInt(args[i]);
            } else if (args[i].equals("-w")) {
                i++;
                width = Integer.parseInt(args[i]);
            } else {
                throw new Error("Usage: java HeatDissipator "
                        + "[ -nrExecutorsPerNode <num> ] "
                        + "[ -minDifference <num> ] ");
            }
        }

        log.info("HeatDissipator, running with dimensions {} x {}:", height, width);

        // Initialize Constellation with the following configuration for an
        // executor.  We create nrExecutorsPerNode on a node.
        ConstellationConfiguration config1 =
                new ConstellationConfiguration(new Context(DivideConquerActivity.LABEL),
                        StealStrategy.SMALLEST, StealStrategy.BIGGEST,
                        StealStrategy.BIGGEST);

        ConstellationConfiguration config2 =
                new ConstellationConfiguration(new Context(StencilOperationActivity.LABEL),
                        StealStrategy.SMALLEST, StealStrategy.BIGGEST,
                        StealStrategy.BIGGEST);

        Constellation constellation =
                ConstellationFactory.createConstellation(config1, config2);

        constellation.activate();

        if (constellation.isMaster()) {
            // This is master specific code.  The rest is going to call
            // Constellation.done(), waiting for Activities to steal.

            HeatValueGenerator heatValueGenerator = new HeatValueGenerator(height, width, 0.2, 100);

            double[][] temp = heatValueGenerator.getTemp();
            double[][] cond = heatValueGenerator.getCond();

            TempChunkResult result;
            HeatRow row = HeatRow.of(temp, cond, 0);

            Timer overallTimer = constellation.getOverallTimer();
            int timing = overallTimer.start();

            do {
                // set up the various activities, staring with the main activity:

                // The SingleEventCollector is an activity that waits for a single
                // event to come in will finish then.
                SingleEventCollector sec = new SingleEventCollector(new Context(DivideConquerActivity.LABEL));

                // submit the single event collector
                ActivityIdentifier aid = constellation.submit(sec);
                // submit the vectorAddActivity. Set the parent as well.
                HeatChunkWithHalo chunk = new HeatChunkWithHalo(row.getTemp(), row.getCond());

                constellation.submit(new DivideConquerActivity(aid, chunk, divideConquerThreshold));

                log.info("main(), just submitted, about to waitForEvent() "
                        + "for any event with target " + aid);
                result = (TempChunkResult) sec.waitForEvent().getData();
                log.info("main(), done with waitForEvent() on identifier " + aid);

                log.info("Performed stencil operation with max temperature delta {}", result.getMaxDifference());

                row = HeatRow.of(result.getTemp(), cond, 0);
            } while (result.getMaxDifference() > minDifference);

            overallTimer.stop(timing);

            writeFile(result.getTemp());
        }
        log.debug("calling Constellation.done()");
        constellation.done();
        log.debug("called Constellation.done()");
    }

}
