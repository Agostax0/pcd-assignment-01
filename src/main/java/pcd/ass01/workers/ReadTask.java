package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ReadTask implements Callable<Void> {

    private final Boid boid;
    private final BoidsModel model;

    ReadTask(final Boid boid, BoidsModel model){
        this.boid = boid;
        this.model = model;
    }

    @Override
    public Void call() {
        this.boid.readNearbyBoids(this.model);
        return null;
    }
}
