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
        if(q <= 0)
            return;
        try {
            mutex.acquire();
            this.quantity = q;
        } catch(InterruptedException mutex_e) {
            //print mutex error
            mutex_e.printStackTrace();
        }
        mutex.release();
    }

    public String getType() {
        return this.type;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int reduceQuantity(int times) {
        int q = 0;

        //critical section
        try {
            mutex.acquire();
            if(this.quantity != 0 && times <= this.quantity) {
                //useless cycle to increment duration of the extraction
                for(int i = 0; i < times; i++)
                    this.quantity--;
                //reduce the amount of shared quantity to local thread quantity
                q = times;
            }
        } catch(InterruptedException mutex_e) {
            //print mutex error
            mutex_e.printStackTrace();
        }
        mutex.release();

        return q;
    }

    public void increaseQuantity(int times) {

        //critical section
        try {
            mutex.acquire();
                //useless cycle to increment duration of the operation
                for(int i = 0; i < times; i++)
                    this.quantity++;
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
