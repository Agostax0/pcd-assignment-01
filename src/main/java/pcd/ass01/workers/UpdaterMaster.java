package pcd.ass01.workers;

import pcd.ass01.BoidsModel;
import pcd.ass01.SynchUtils.MyCyclicBarrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class UpdaterMaster {

    public UpdaterMaster() {

    }

    public void update(BoidsModel model) {
        var readers = new ArrayList<Thread>();

        for(int i = 0; i < model.getBoids().size(); i++){
            var boid = model.getBoids().get(i);
            var worker = Thread.ofVirtual().unstarted(() ->{
                boid.readNearbyBoids(model);
            });
            worker.start();

            readers.add(worker);
        }

        readers.forEach(it -> {
            try {
                it.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        var writers = new ArrayList<Thread>();

        for(int i = 0; i < model.getBoids().size(); i++){
            var boid = model.getBoids().get(i);
            var worker = Thread.ofVirtual().unstarted(() ->{
                boid.updateVelocity(model);
                boid.updatePos(model);
            });
            worker.start();

            writers.add(worker);
        }


        writers.forEach(it -> {
            try {
                it.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private synchronized void log(String msg) {
        //System.out.println("[Thread: main ] " + msg);
    }
}
