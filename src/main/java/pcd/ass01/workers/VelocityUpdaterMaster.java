package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.BoidsPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class VelocityUpdaterMaster{
    private int availableProcessors = Runtime.getRuntime().availableProcessors();

    private List<Boid> allBoids;

    private BoidsModel model;
    private List<VelocityUpdaterWorker> workers = new ArrayList<>();

    private Semaphore allUpdatersFinished = new Semaphore(availableProcessors, true);

    public VelocityUpdaterMaster(){
        await();

        for(int i = 0; i < availableProcessors; i++){
            var worker = new VelocityUpdaterWorker(allUpdatersFinished);
            workers.add(worker);
            worker.start();
        }
    }

    public void setAllBoids(List<Boid> allBoids) {
        this.allBoids = allBoids;
    }

    public void setModel(BoidsModel model) {
        this.model = model;
    }

    public void await(){
        try {
            allUpdatersFinished.acquire(availableProcessors);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void signalAll(){
        allUpdatersFinished.release(availableProcessors);
    }

    public void updateVelocities(){
        int partitionSize = allBoids.size() / availableProcessors;

        for(int index = 0; index < availableProcessors; index++){
            var boidPartition = allBoids.subList(index * partitionSize, index * partitionSize + partitionSize);

            var worker = workers.get(index);
            worker.setBoidPartition(boidPartition);
        }

        signalAll();
    }
    
    private class VelocityUpdaterWorker extends Thread{

        private final Semaphore workDoneSemaphore;
        private List<Boid> boidPartition;

        public VelocityUpdaterWorker(Semaphore workDoneSemaphore) {
            this.workDoneSemaphore = workDoneSemaphore;
        }

        public void setBoidPartition(List<Boid> boidPartition) {
            this.boidPartition = boidPartition;
        }

        public void run(){
            while(true){
                try {
                    this.workDoneSemaphore.acquire();

                    for (Boid boid : this.boidPartition) {
                        boid.updateVelocity(model);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    this.workDoneSemaphore.release();
                }
            }
        }
    }

}
