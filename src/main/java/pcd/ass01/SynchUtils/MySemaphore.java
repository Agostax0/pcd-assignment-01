package pcd.ass01.SynchUtils;

public class MySemaphore {

    private final int totalPermits;

    private int currentPermits;

    public MySemaphore(int permits){
        this.totalPermits = permits;
    }

    public synchronized void acquire(final int permitsRequested) throws InterruptedException {
        while (this.currentPermits < permitsRequested){
            wait();
        }
        this.currentPermits -= permitsRequested;
    }

    public synchronized void acquire() throws InterruptedException {
        while (this.currentPermits == 0){
            wait();
        }
        this.currentPermits--;
    }

    public synchronized void release(final int releasedPermits){
        this.currentPermits = Math.min(this.currentPermits + releasedPermits, this.totalPermits);
        for(int i = 0; i < releasedPermits; i++){
            notify();
        }
    }

    public synchronized void release(){
        this.currentPermits = Math.min(this.currentPermits + 1, this.totalPermits);
        notify();
    }

}
