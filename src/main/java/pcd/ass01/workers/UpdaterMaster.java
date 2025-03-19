package pcd.ass01.workers;

import pcd.ass01.BoidsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class UpdaterMaster {


    private final Semaphore readSemaphore;
    private final Semaphore writeSemaphore;
    private final Semaphore updateSemaphore;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();

    private final List<Worker> workers = new ArrayList<>();

    public UpdaterMaster(){

        //this.readBarrier = new CyclicBarrier(availableProcessors);
        this.readSemaphore = new Semaphore(availableProcessors, true);

        //this.writeBarrier = new CyclicBarrier(availableProcessors);
        this.writeSemaphore = new Semaphore(availableProcessors, true);

        //this.updateBarrier = new CyclicBarrier(availableProcessors);
        this.updateSemaphore = new Semaphore(availableProcessors, true);

        blockSemaphore(this.readSemaphore);
        blockSemaphore(this.writeSemaphore);
        blockSemaphore(this.updateSemaphore);

        for (int index = 0; index < availableProcessors; index ++){
            Worker worker = new Worker(index, readSemaphore, writeSemaphore, updateSemaphore);
            workers.add(worker);
            worker.start();
        }

    }

    public void update(BoidsModel model){
        this.workers.forEach(it -> it.setModel(model));

        log("start read");
        this.readSemaphore.release(availableProcessors);
        blockSemaphore(this.readSemaphore);
        log("end read");


        log("start write");
        this.writeSemaphore.release(availableProcessors);
        blockSemaphore(this.writeSemaphore);
        log("end write");

        log("start update");
        this.updateSemaphore.release(availableProcessors);
        blockSemaphore(this.updateSemaphore);
        log("end update");

    }

    private void blockSemaphore(Semaphore semaphore){
        try {
            semaphore.acquire(availableProcessors);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void log(String msg){
        //System.out.println("[Thread: main ]" + msg);
    }
}
