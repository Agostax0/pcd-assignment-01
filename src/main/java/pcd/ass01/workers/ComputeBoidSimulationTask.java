package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.concurrent.Callable;

public class ComputeBoidSimulationTask implements Callable<Void> {

    private final Boid boid;
    private final BoidsModel model;

    ComputeBoidSimulationTask(final Boid boid, BoidsModel model){
        this.boid = boid;
        this.model = model;
    }
    @Override
    public Void call() {
        this.boid.readNearbyBoids(this.model);

        this.boid.updateVelocity(this.model);

        this.boid.updatePos(this.model);

        return null;
    }
}
