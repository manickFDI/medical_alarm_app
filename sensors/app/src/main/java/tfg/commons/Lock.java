package tfg.commons;

/**
 * Created by Light on 28/11/15.
 */
public class Lock{

    private boolean isLocked = false;

    public synchronized void lock()
            throws InterruptedException{
        while(isLocked){
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock(){
        isLocked = false;
        notify();
    }
}
