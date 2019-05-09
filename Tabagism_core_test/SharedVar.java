import java.util.concurrent.Semaphore;



public class SharedVar {
    private int state;
    private Semaphore mutex;

    public SharedVar() {
        this.state = 0;
        this.mutex = new Semaphore(1);
    }

    public SharedVar(int state) {
        this.state = state;
        this.mutex = new Semaphore(1);
    }

    public SharedVar(int state, Semaphore sem) {
        this.state = state;
        this.mutex = sem;
    }

    public int get() {
        int x = 0;

        try {
            this.mutex.acquire();
            x = this.state;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        this.mutex.release();

        return state;
    }

    public void set(int x) {
        try {
            this.mutex.acquire();
            this.state = x;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        this.mutex.release();
    }

    public void increase(int x) {
        try {
            this.mutex.acquire();
            this.state += x;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        this.mutex.release();
    }

    public void increase() {
        try {
            this.mutex.acquire();
            this.state++;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        this.mutex.release();
    }

    public void decrease(int x) {
        try {
            this.mutex.acquire();
            this.state -= x;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        this.mutex.release();
    }

    public void decrease() {
        try {
            this.mutex.acquire();
            this.state--;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        this.mutex.release();
    }
}
