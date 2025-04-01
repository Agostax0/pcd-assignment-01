package pcd.ass01.workers;

import pcd.ass01.BoidsModel;
import pcd.ass01.SynchUtils.MyCyclicBarrier;

import java.util.ArrayList;
import java.util.List;

public class UpdaterMaster {

    private final int availableProcessors;

    private final List<Worker> workers = new ArrayList<>();
    private final MyCyclicBarrier startBarrier;
    private final MyCyclicBarrier endBarrier;

    public UpdaterMaster(int threadCount) {
        availableProcessors = (threadCount > 0)
                ? Math.min(threadCount,Runtime.getRuntime().availableProcessors())
                : Runtime.getRuntime().availableProcessors();

        this.startBarrier = new MyCyclicBarrier(availableProcessors + 1);
        this.endBarrier = new MyCyclicBarrier(availableProcessors + 1);

        var readToWriteBarrier = new MyCyclicBarrier(availableProcessors);

        for (int index = 0; index < availableProcessors; index++) {
            Worker worker = new Worker(index, this.startBarrier, this.endBarrier, readToWriteBarrier);
            workers.add(worker);
            worker.start();
        }
    }

    public void update(BoidsModel model) {
        this.workers.forEach(it -> it.setModel(model));
        try {
            this.startBarrier.await(); //quando il main esegue questo await gli altri thread partono
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            this.endBarrier.await(); //quando gli altri thread finiscono libero il main
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
