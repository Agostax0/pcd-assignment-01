package pcd.ass01;

import pcd.ass01.workers.PositionUpdateMaster;
import pcd.ass01.workers.UpdaterMaster;
import pcd.ass01.workers.VelocityUpdaterMaster;

import javax.swing.text.Position;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;

    private final VelocityUpdaterMaster velocityUpdaterMaster;
    private final PositionUpdateMaster positionUpdateMaster;
    private final UpdaterMaster updaterMaster;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();

        velocityUpdaterMaster = new VelocityUpdaterMaster();
        positionUpdateMaster = new PositionUpdateMaster();
        updaterMaster = new UpdaterMaster();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {
            var t0 = System.currentTimeMillis();

            updaterMaster.update(model);

            var t1 = System.currentTimeMillis();
            var dtElapsed = t1 - t0;

            //System.out.println("dtElapsed: " + dtElapsed);

            if (view.isPresent()) {
                view.get().update(framerate);
                var framratePeriod = 1000/FRAMERATE;


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

    private synchronized void log(String msg){

    }
}
