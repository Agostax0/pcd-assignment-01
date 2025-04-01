package pcd.ass01;

import pcd.ass01.workers.UpdaterMaster;

public class BoidsSimulator {

    private BoidsModel model;

    private final UpdaterMaster updaterMaster;

    private long startTime;

    public BoidsSimulator(BoidsModel model, int threadCount) {
        startTime = System.currentTimeMillis();
        this.model = model;
        updaterMaster = new UpdaterMaster(threadCount);
    }
      
    public void runSimulation() {
        var iteration = 0;
    	while (iteration++ < 1500) {
                updaterMaster.update(model);
    	}
        System.out.println(System.currentTimeMillis() - startTime);
    }
}
