import java.util.concurrent.Semaphore;



public class Component {

    private String type;
    private int quantity;
    Semaphore mutex = new Semaphore(1);

    public Component(String t) {
        this.type = t;
        this.quantity = 0;
    }

    public Component(String t, int q) {
        this.type = t;
        this.quantity = q;
    }

    public void setType(String t) {
        this.type = t;
    }

    public void setQuantity(int q) {
        if(q <= 0)
            return;
        try {
            this.mutex.acquire();
            this.quantity = q;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        mutex.release();
    }

    public String getType() {
        return this.type;
    }

    public int getQuantity() {
        int q = 0;

        try {
            this.mutex.acquire();
            q = this.quantity;
        } catch(InterruptedException mutex_e) {
            mutex_e.printStackTrace();
        }
        mutex.release();
        
        return q;
    }

    public int decreaseQuantity(int times) {
        int q = 0;

        //critical section
        try {
            this.mutex.acquire();
            if(this.quantity != 0 && times <= this.quantity) {
                this.quantity -= times;
                //reduce the amount of shared quantity to local thread quantity
                q = times;
            }
        } catch(InterruptedException mutex_e) {
            //print mutex error
            mutex_e.printStackTrace();
        }
        this.mutex.release();

        return q;
    }

    public void increaseQuantity(int times) {
        //critical section
        try {
            mutex.acquire();
            this.quantity += times;
        } catch(InterruptedException mutex_e) {
            //print mutex error
            mutex_e.printStackTrace();
        }
        mutex.release();
    }

    public void printInfo() {
        System.out.println("\tComponent "+this.type+" = "+this.quantity);
    }

}
