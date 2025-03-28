package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.concurrent.Callable;

public class WriteTask implements Callable<Void> {
    private final Boid boid;
    private final BoidsModel model;
    public WriteTask(Boid boid, BoidsModel model) {
        this.boid = boid;
        this.model = model;
    }

    @Override
    public Void call() {
        this.boid.updateVelocity(this.model);
        this.boid.updatePos(this.model);
        return null;
    }
}
