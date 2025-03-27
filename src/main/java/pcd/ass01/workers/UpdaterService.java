package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class UpdaterService {
    private ExecutorService executor;
    private int numTasks;


    public UpdaterService() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    public void compute(final BoidsModel model) {
        this.numTasks = model.getBoids().size();

        List<Callable<Object>> results = new LinkedList<>();

        for (int taskIndex = 0; taskIndex < this.numTasks; taskIndex++) {
            var boid = model.getBoids().get(taskIndex);
            Callable<Object> res = new ComputeBoidSimulationTask(boid, model);
            results.add(res);
        }

        try {
            executor.invokeAll(results);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
