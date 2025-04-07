package pcd.ass01;

import java.util.Optional;
import pcd.ass01.workers.UpdaterService;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;

    private final UpdaterService updaterService;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
        updaterService = new UpdaterService();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
        var t0 = System.currentTimeMillis();
        while (true) {

            if(!model.isModelPaused()) {

                updaterService.compute(model);

            }

            if (view.isPresent()) {
                view.get().update(framerate);
                var framratePeriod = 1000 / FRAMERATE;

                var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                if (dtElapsed < framratePeriod) {
                    try {
                        Thread.sleep(framratePeriod - dtElapsed);
                    } catch (Exception ex) {
                    }
                    framerate = FRAMERATE;
                } else {
                    framerate = (int) (1000 / dtElapsed);
                }
            }
        }
    }

    private synchronized void log(String msg){

    }
}
