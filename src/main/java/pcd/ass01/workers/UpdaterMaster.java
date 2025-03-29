package pcd.ass01.workers;

import pcd.ass01.BoidsModel;
import pcd.ass01.SynchUtils.MyCyclicBarrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class UpdaterMaster {

    private final int availableProcessors = Runtime.getRuntime().availableProcessors();

    private final List<Worker> workers = new ArrayList<>();
    private final MyCyclicBarrier startBarrier;
    private final MyCyclicBarrier endBarrier;

    public UpdaterMaster() {

        this.startBarrier = new MyCyclicBarrier(availableProcessors + 1, "Start Barrier");
        this.endBarrier = new MyCyclicBarrier(availableProcessors + 1);

        var readToWriteBarrier = new MyCyclicBarrier(availableProcessors);

        for (int index = 0; index < availableProcessors; index++) {
            Worker worker = new Worker(index, this.startBarrier, this.endBarrier, readToWriteBarrier);
            workers.add(worker);
            worker.start();
        }
    }

    public void update(BoidsModel model) {
        log("li aggiorno");
        this.workers.forEach(it -> it.setModel(model));
        log("segnalo di partire ai worker [" + this.startBarrier.getQueuePosition() + "]");
        try {
            this.startBarrier.await(); //quando il main esegue questo await gli altri thread partono
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            log("aspetto i worker " + this.endBarrier.getQueuePosition());
            this.endBarrier.await(); //quando gli altri thread finiscono libero il main
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void log(String msg) {
        //System.out.println("[Thread: main ] " + msg);
    }
}
