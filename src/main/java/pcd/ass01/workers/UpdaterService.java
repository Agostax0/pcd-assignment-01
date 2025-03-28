package pcd.ass01.workers;

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

        List<Future<Void>> reads = new LinkedList<>();

        for (int taskIndex = 0; taskIndex < this.numTasks; taskIndex++) {
            var boid = model.getBoids().get(taskIndex);
            Future<Void> res = executor.submit(new ReadTask(boid, model));
            reads.add(res);
        }

        reads.forEach(it -> {
            try {
                it.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        List<Future<Void>> writes = new LinkedList<>();


        for (int taskIndex = 0; taskIndex < this.numTasks; taskIndex++) {
            var boid = model.getBoids().get(taskIndex);
            Future<Void> res = executor.submit(new WriteTask(boid, model));
            writes.add(res);
        }

        writes.forEach(it -> {
            try {
                it.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
