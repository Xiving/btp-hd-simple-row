package btp.hd.cji;

public class HeatDissipator {

    public static void main(String[] args) {
        // this code is executed on every node

        // the number of executors per node in the cluster
        int nrExecutorsPerNode = 4;

        // the threshold to decide whether to compute or divide tasks
        int computeDivideThreshold = 256;
    }

}
