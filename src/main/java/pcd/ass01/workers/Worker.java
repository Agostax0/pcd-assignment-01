package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Worker extends Thread {

    private final int index;

    private BoidsModel model;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private List<Boid> myBoids;

    public Worker(final int index) {
        this.index = index;
    }


    public void setModel(BoidsModel model) {
        this.model = model;

        final int partitionSize = model.getBoids().size() / availableProcessors;
        this.myBoids = model.getBoids().subList(this.index * partitionSize, this.index * partitionSize + partitionSize);
    }

    public void run() {

        while (true) {

        }
    }

    private synchronized void log(String msg) {
        //System.out.println("[Thread: " + index + " ] " +msg);
    }
}
