package pcd.ass01.workers;

import pcd.ass01.BoidsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class UpdaterMaster {


    private final CyclicBarrier readBarrier;
    private final CyclicBarrier writeBarrier;
    private final CyclicBarrier updateBarrier;

    private final Semaphore blockingWorkersSemaphore;

    private final int availableProcessors = Runtime.getRuntime().availableProcessors();

    private final List<Worker> workers = new ArrayList<>();

    public UpdaterMaster(){

        this.readBarrier = new CyclicBarrier(availableProcessors);

        this.writeBarrier = new CyclicBarrier(availableProcessors);

        this.updateBarrier = new CyclicBarrier(availableProcessors);

        this.blockingWorkersSemaphore = new Semaphore(availableProcessors, false);

        blockSemaphore(this.blockingWorkersSemaphore);

        for (int index = 0; index < availableProcessors; index ++){
            Worker worker = new Worker(index, readBarrier, writeBarrier, updateBarrier, this.blockingWorkersSemaphore);
            workers.add(worker);
            worker.start();
        }

    }

    public void update(BoidsModel model){
        this.workers.forEach(it -> it.setModel(model));

        releaseAllWorkers();
    }

    private void releaseAllWorkers() {
        this.blockingWorkersSemaphore.release(availableProcessors);
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
