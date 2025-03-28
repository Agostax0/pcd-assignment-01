package pcd.ass01.workers;

import pcd.ass01.BoidsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class UpdaterMaster {

    private final int availableProcessors = Runtime.getRuntime().availableProcessors();

    private final List<Worker> workers = new ArrayList<>();

    public UpdaterMaster() {
        for (int index = 0; index < availableProcessors; index++) {
            Worker worker = new Worker(index);
            workers.add(worker);
            worker.start();
        }

    }

    public void update(BoidsModel model) {
        this.workers.forEach(it -> it.setModel(model));

    }

    private synchronized void log(String msg) {
        //System.out.println("[Thread: main ]" + msg);
    }
}
