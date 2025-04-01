package pcd.ass01;

import pcd.ass01.workers.UpdaterService;

public class BoidsSimulator {

    private BoidsModel model;

    private final long startTime;

    private final UpdaterService updaterService;

    public BoidsSimulator(BoidsModel model) {
        startTime = System.currentTimeMillis();
        this.model = model;
        updaterService = new UpdaterService();
    }


    public void runSimulation() {
        var iteration = 0;
    	while (iteration++ < 1500) {
                updaterService.compute(model);
    	}
        System.out.println(System.currentTimeMillis()-startTime);
    }

}
