package pcd.ass01.SynchUtils;

public class MyCyclicBarrier {
    private final int parties;
    private int currentParties = 0;
    private String name = null;

    private boolean isBroken = false;

    public MyCyclicBarrier(final int parties){
        this.parties = parties;
    }

    public MyCyclicBarrier(final int parties, final String name){
        this.parties = parties;
        this.name = name;
    }

    public synchronized void await() throws InterruptedException {
        this.isBroken = false;
        this.currentParties++;

        if(this.currentParties == this.parties){
            this.isBroken = true;
            //tutti hanno raggiunto la barriera
            notifyAll();
            this.currentParties = 0;
        }
        else{
            while(this.currentParties < this.parties && !isBroken){
                //aspetto gli altri
                wait();
            }
        }
    }

}
