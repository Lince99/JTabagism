package Tabagism/Tabagism_core;

public class Shop extends Thread {

    Semaphore sem;
    String t_name;

    public Shop(Semaphore s, String t) {
        this.sem = s;
        this.t_name = t;
    }

    @Override
    public void run() {
        //shop do stuff
    }
}
