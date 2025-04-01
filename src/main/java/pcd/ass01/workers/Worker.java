package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.SynchUtils.MyCyclicBarrier;

import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread {

    private final int index;

    private BoidsModel model;
    private final int availableProcessors;
    private List<Boid> myBoids = new ArrayList<>();
    private final MyCyclicBarrier startBarrier;
    private final MyCyclicBarrier endBarrier;

    private final MyCyclicBarrier readToWriteBarrier;

    public Worker(final int index, final MyCyclicBarrier startBarrier, final MyCyclicBarrier endBarrier, final MyCyclicBarrier readToWriteBarrier, final int threadCount) {
        this.index = index;
        this.startBarrier = startBarrier;
        this.endBarrier = endBarrier;
        this.readToWriteBarrier = readToWriteBarrier;
        this.availableProcessors = threadCount > 0 ? Math.min(threadCount, Runtime.getRuntime().availableProcessors()) : Runtime.getRuntime().availableProcessors();
    }


    public void setModel(BoidsModel model) {
        this.model = model;

        myBoids.clear();

        for(int i= 0; i < model.getBoids().size(); i++){
            if(i % availableProcessors == index){
                myBoids.add(model.getBoids().get(i));
            }
        }
    }

    public void run() {

        while (true) {
            awaitStart();

            readMyBoids();

            awaitReadToThenWrite();
            
            writeMyBoids();

            signalEnd();
        }
    }

    private void awaitReadToThenWrite() {
        try {
            this.readToWriteBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeMyBoids() {
        for(var boid: myBoids) {
            boid.updateVelocity(model);
            boid.updatePos(model);
        }
    }

    private void readMyBoids() {
        for(var boid: myBoids) boid.readNearbyBoids(this.model);
    }

    private void signalEnd() {
        try {
            this.endBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void awaitStart() {
        try {
            this.startBarrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
