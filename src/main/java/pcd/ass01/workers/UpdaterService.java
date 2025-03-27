package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdaterService extends Thread{
    private ExecutorService executor;
    private int numTasks;


    public UpdaterService(){
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    public void compute(final BoidsModel model){
        this.numTasks = model.getBoids().size();

        List<Future<Void>> results = new LinkedList<>();

        for(int taskIndex = 0 ; taskIndex < this.numTasks ; taskIndex++){
            var boid = model.getBoids().get(taskIndex);
            Future<Void> res = executor.submit(new ComputeBoidSimulationTask(boid, model));
            results.add(res);
        }

        for (var res : results){
            try {
                res.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
