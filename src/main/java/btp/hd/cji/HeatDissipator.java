package btp.hd.cji;

import btp.hd.cji.Activity.HeatRowActivity;
import btp.hd.cji.model.HeatRowResult;
import btp.hd.cji.util.PgmReader;
import ibis.constellation.*;
import ibis.constellation.util.SingleEventCollector;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.PrintStream;

@Slf4j
public class HeatDissipator {

    public static void writeFile(float[] array) {
        try {
            PrintStream out = new PrintStream("heat-dissipator.out");
            for (int i = 0; i < array.length; i++) {
                out.println(array[i]);
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        // this code is executed on every node

        // the number of executors per node in the cluster
        int nrExecutorsPerNode = 4;

        // the threshold to decide whether to compute or divide tasks
        int threshold = 256;

        // the temperature values
        double[][] temp = null;

        // the conductivity values
        double[][] cond;

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
            } else if (args[i].equals("-threshold")) {
                i++;
                threshold = Integer.parseInt(args[i]);
            } else if (args[i].equals("-tempFile")) {
                i++;
                temp = PgmReader.read(args[i]);
            } else if (args[i].equals("-condFile")) {
                i++;
                cond = PgmReader.read(args[i]);
            } else {
                throw new Error("Usage: java HeatDissipator "
                        + "[ -nrExecutorsPerNode <num> ] "
                        + "[ -tempFile <file> ]"
                        + "[ -condFile <file> ]"
                        + "[ -threshold <num> ] ");
            }
        }

        // Initialize Constellation with the following configuration for an
        // executor.  We create nrExecutorsPerNode on a node.
        ConstellationConfiguration config =
                new ConstellationConfiguration(new Context(HeatRowActivity.LABEL),
                        StealStrategy.SMALLEST, StealStrategy.BIGGEST,
                        StealStrategy.BIGGEST);

        Constellation constellation =
                ConstellationFactory.createConstellation(config, nrExecutorsPerNode);

        constellation.activate();

        if (constellation.isMaster()) {
            // This is master specific code.  The rest is going to call
            // Constellation.done(), waiting for Activities to steal.

            System.out.println("HeatDissipator, running with n x n: " + temp.length + " x " + temp[0].length);


            Timer overallTimer = constellation.getOverallTimer();
            int timing = overallTimer.start();

            // set up the various activities, staring with the main activity:

            // The SingleEventCollector is an activity that waits for a single
            // event to come in will finish then.
            SingleEventCollector sec = new SingleEventCollector(new Context(HeatRowActivity.LABEL));

            // submit the single event collector
            ActivityIdentifier aid = constellation.submit(sec);
            // submit the vectorAddActivity. Set the parent as well.
            constellation.submit(new HeatRowActivity(aid, computeDivideThreshold, n, a, b));

            log.debug("main(), just submitted, about to waitForEvent() "
                    + "for any event with target " + aid);
            HeatRowResult result = (HeatRowResult) sec.waitForEvent().getData();
            log.debug("main(), done with waitForEvent() on identifier " + aid);

            overallTimer.stop(timing);

            writeFile(result.c);
        }
        log.debug("calling Constellation.done()");
        constellation.done();
        log.debug("called Constellation.done()");
    }

}
