package Tabagism.Tabagism_core;

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
        this.quantity = q;
    }

    public String getType() {
        return this.type;
    }

    public synchronized int getQuantity() {
        int q = 0;

        //critical section
        try {
            mutex.acquire();
            if(this.quantity != 0) {
                q = this.quantity;
                //reduce the amount of shared quantity to local thread quantity
                this.quantity -= q;
            }
        } catch(InterruptedException mutex_e) {
            //print mutex error
        }
        mutex.release();

        return q;
    }

    //TODO CONVERT TO ARRAYLIST
    /*public Component[] initComponentArr(int dim, String[] names, int[] q) {
        Component res[dim];

        for(int i = 0; i < dim; i++) {
            res[i] = new Component(names[i], q[i]);
        }

        return res;
    }*/
}
