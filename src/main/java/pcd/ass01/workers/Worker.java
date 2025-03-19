package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.List;
import java.util.concurrent.Semaphore;

public class Worker extends Thread{

    private final int index;
    private final Semaphore readSemaphore;
    private final Semaphore writeSemaphore;
    private final Semaphore updateSemaphore;
    private BoidsModel model;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private List<Boid> myBoids;

    public Worker(final int index, final Semaphore readSemaphore, final Semaphore writeSemaphore, final Semaphore updateSemaphore) {
        this.index = index;
        this.readSemaphore = readSemaphore;
        this.writeSemaphore = writeSemaphore;
        this.updateSemaphore = updateSemaphore;
    }


    public void setModel(BoidsModel model) {
        this.model = model;

        final int partitionSize = model.getBoids().size() / availableProcessors;
        this.myBoids = model.getBoids().subList(this.index * partitionSize, this.index * partitionSize + partitionSize);
    }

    public void run(){

        read();

        write();

        update();

    }

    private void update() {
        try {
            this.updateSemaphore.acquire();
            log("acquired update");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            //update
            for(var boid: myBoids) boid.updatePos(model);
            this.updateSemaphore.release();
            log("release update");
        }
    }

    private void write() {
        try {
            this.writeSemaphore.acquire();
            log("acquired write");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            //write
            for(var boid: myBoids) boid.updateVelocity(model);
            this.writeSemaphore.release();
            log("released write");
        }
    }

    private void read(){
        try {
            this.readSemaphore.acquire();
            log("acquired read");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            //read
            for(var boid: myBoids) boid.readNearbyBoids(model);
            this.readSemaphore.release();
            log("released read");
        }
    }

    private void getNearbyBoids(){

    }

    private synchronized void log(String msg){
        System.out.println("[Thread: " + index + " ] " +msg);
    }
}
