package pcd.ass01;

import java.util.List;

public interface Boid {
    P2d getPos();

    V2d getVel();

    void update(BoidsModel model);

    List<Boid> getNearbyBoids(BoidsModel model);

    V2d calculateAlignment(List<Boid> nearbyBoids, BoidsModel model);

    V2d calculateCohesion(List<Boid> nearbyBoids, BoidsModel model);

    V2d calculateSeparation(List<Boid> nearbyBoids, BoidsModel model);
}
