package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Worker extends Thread{

    private final int index;
    private final CyclicBarrier readBarrier;
    private final CyclicBarrier writeBarrier;
    private final CyclicBarrier updateBarrier;
    private final Semaphore isWorkerBlockedSemaphore;

    private BoidsModel model;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private List<Boid> myBoids;

    public Worker(final int index, final CyclicBarrier readBarrier, final CyclicBarrier writeBarrier, final CyclicBarrier updateBarrier, final Semaphore isWorkerBlockedSemaphore) {
        this.index = index;
        this.readBarrier = readBarrier;
        this.writeBarrier = writeBarrier;
        this.updateBarrier = updateBarrier;
        this.isWorkerBlockedSemaphore = isWorkerBlockedSemaphore;
    }


    public void setModel(BoidsModel model) {
        this.model = model;

        final int partitionSize = model.getBoids().size() / availableProcessors;
        this.myBoids = model.getBoids().subList(this.index * partitionSize, this.index * partitionSize + partitionSize);
    }

    public void run(){

        while(true){

            awaitStart();

            read();

            write();

            update();

        }
    }

    private void update() {
        try {
            this.updateBarrier.await();
            log("started update");
            for(var boid: myBoids) boid.updatePos(model);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        finally {
            log("release update");

        }
    }

    private void write() {
        try {
            this.writeBarrier.await();
            log("started write");
            for (Boid boid : myBoids) {boid.updateVelocity(this.model);}
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        finally {
            log("released write");
        }
    }

    private void read(){
        try {
            this.readBarrier.await();
            log("acquired read");
            for(var boid: myBoids) boid.readNearbyBoids(model);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        } finally {
            log("released read");
        }
    }

    private void awaitStart(){
        try {
            this.isWorkerBlockedSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void log(String msg){
        //System.out.println("[Thread: " + index + " ] " +msg);
    }
}
