package pcd.ass01;

import pcd.ass01.workers.UpdaterMaster;

import java.util.Optional;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 60;
    private int framerate;

    private final UpdaterMaster updaterMaster;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
        updaterMaster = new UpdaterMaster();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {

            if(!model.isModelPaused()){

                var t0 = System.currentTimeMillis();

                updaterMaster.update(model);



                if (view.isPresent()) {
                    view.get().update(framerate);
                    var framratePeriod = 1000/FRAMERATE;

                    var t1 = System.currentTimeMillis();
                    var dtElapsed = t1 - t0;

                    System.out.println("dtElapsed: " + dtElapsed);

                    if (dtElapsed < framratePeriod) {
                        try {
                            Thread.sleep(framratePeriod - dtElapsed);
                        } catch (Exception ex) {}
                        framerate = FRAMERATE;
                    } else {
                        framerate = (int) (1000/dtElapsed);
                    }
                }
            }
    	}
    }

    private synchronized void log(String msg){

    }
}
