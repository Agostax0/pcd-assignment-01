package pcd.ass01;

import pcd.ass01.workers.VelocityUpdaterMaster;

import java.util.Optional;
import java.util.concurrent.Semaphore;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;

    private final VelocityUpdaterMaster velocityUpdaterMaster;
    
    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();

        velocityUpdaterMaster = new VelocityUpdaterMaster();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
    	while (true) {
            var t0 = System.currentTimeMillis();
    		var boids = model.getBoids();
    		/*
    		for (Boid boid : boids) {
                boid.update(model);
            }
            */


            velocityUpdaterMaster.setAllBoids(boids);
            velocityUpdaterMaster.setModel(model);

            velocityUpdaterMaster.updateVelocities();

            /*
    		 * ..then update positions
    		 */
    		for (Boid boid : boids) {
                boid.updatePos(model);
            }

            
    		if (view.isPresent()) {
            	view.get().update(framerate);
            	var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                var framratePeriod = 1000/FRAMERATE;

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
