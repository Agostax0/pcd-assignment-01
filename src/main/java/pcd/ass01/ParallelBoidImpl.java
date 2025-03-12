package pcd.ass01;

import java.util.*;
import java.util.concurrent.Semaphore;

public class ParallelBoidImpl implements Boid {
    private P2d pos;
    private V2d vel;

    private final int availableProcessors = Runtime.getRuntime().availableProcessors();

    public ParallelBoidImpl(P2d pos, V2d vel) {
        this.pos = pos;
        this.vel = vel;
    }

    @Override
    public P2d getPos() {
        return pos;
    }

    @Override
    public V2d getVel() {
        return vel;
    }


    @Override
    public void update(BoidsModel model) {

        List<Boid> nearbyBoids = getNearbyBoids(model);

        V2d separation = calculateSeparation(nearbyBoids, model);
        V2d alignment = calculateAlignment(nearbyBoids, model);
        V2d cohesion = calculateCohesion(nearbyBoids, model);

        vel = vel.sum(alignment.mul(model.getAlignmentWeight()))
                .sum(separation.mul(model.getSeparationWeight()))
                .sum(cohesion.mul(model.getCohesionWeight()));

        /* Limit speed to MAX_SPEED */

        double speed = vel.abs();

        if (speed > model.getMaxSpeed()) {
            vel = vel.getNormalized().mul(model.getMaxSpeed());
        }

        /* Update position */

        pos = pos.sum(vel);

        /* environment wrap-around */

        if (pos.x() < model.getMinX()) pos = pos.sum(new V2d(model.getWidth(), 0));
        if (pos.x() >= model.getMaxX()) pos = pos.sum(new V2d(-model.getWidth(), 0));
        if (pos.y() < model.getMinY()) pos = pos.sum(new V2d(0, model.getHeight()));
        if (pos.y() >= model.getMaxY()) pos = pos.sum(new V2d(0, -model.getHeight()));
    }

    private class NearbyBoidsWorker extends Thread {
        private final Boid myBoid;
        private final List<Boid> allBoids;
        private final Set<Boid> res;
        private final double perceptionRadius;
        private final Semaphore finishedWork;

        private NearbyBoidsWorker(Boid myBoid, List<Boid> allBoids, Set<Boid> res, double perceptionRadius, Semaphore finishedWork) {
            this.myBoid = myBoid;
            this.allBoids = allBoids;
            this.res = res;
            this.perceptionRadius = perceptionRadius;
            this.finishedWork = finishedWork;
        }

        public void run() {
            try {
                finishedWork.acquire();


                for (Boid other : allBoids) {
                    if (myBoid != other) {
                        P2d otherPos = other.getPos();
                        double distance = otherPos.distance(myBoid.getPos());
                        if (distance < perceptionRadius) {
                            res.add(other);
                        }
                    }
                }


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                finishedWork.release();
            }
        }


    }

    @Override
    public List<Boid> getNearbyBoids(BoidsModel model) {

        final int partitionSize = model.getBoids().size() / availableProcessors;

        Set<Boid> res = Collections.synchronizedSet(new HashSet<>());

        Semaphore workDoneSemaphore = new Semaphore(availableProcessors, false);

        for (int index = 0; index < availableProcessors; index++) {

            var boidPartition = model.getBoids().subList(index * partitionSize, index * partitionSize + partitionSize);

            new NearbyBoidsWorker(this, boidPartition, res, model.getPerceptionRadius(), workDoneSemaphore).start();

        }

        try {
            workDoneSemaphore.acquire(availableProcessors);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workDoneSemaphore.release();
        }

        synchronized (res) {
            return res.stream().toList();
        }
    }

    @Override
    public V2d calculateAlignment(List<Boid> nearbyBoids, BoidsModel model) {
        double avgVx = 0;
        double avgVy = 0;
        if (nearbyBoids.size() > 0) {
            for (Boid other : nearbyBoids) {
                V2d otherVel = other.getVel();
                avgVx += otherVel.x();
                avgVy += otherVel.y();
            }
            avgVx /= nearbyBoids.size();
            avgVy /= nearbyBoids.size();
            return new V2d(avgVx - vel.x(), avgVy - vel.y()).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }

    @Override
    public V2d calculateCohesion(List<Boid> nearbyBoids, BoidsModel model) {
        double centerX = 0;
        double centerY = 0;
        if (nearbyBoids.size() > 0) {
            for (Boid other : nearbyBoids) {
                P2d otherPos = other.getPos();
                centerX += otherPos.x();
                centerY += otherPos.y();
            }
            centerX /= nearbyBoids.size();
            centerY /= nearbyBoids.size();
            return new V2d(centerX - pos.x(), centerY - pos.y()).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }

    @Override
    public V2d calculateSeparation(List<Boid> nearbyBoids, BoidsModel model) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (Boid other : nearbyBoids) {
            P2d otherPos = other.getPos();
            double distance = pos.distance(otherPos);
            if (distance < model.getAvoidRadius()) {
                dx += pos.x() - otherPos.x();
                dy += pos.y() - otherPos.y();
                count++;
            }
        }
        if (count > 0) {
            dx /= count;
            dy /= count;
            return new V2d(dx, dy).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }
}
