package Tabagism.Tabagism_core;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;



public class Shop extends Thread {

    ArrayList public_resource;
    private ArrayList local_resource;
    Semaphore lock_risorse;
    private String t_name;
    private int change_time;

    public Shop(ArrayList components, String name) {
        this.public_resource = components;
        this.local_resource = new ArrayList();
        this.lock_risorse = new Semaphore(1);
        this.t_name = name;
        this.change_time = 0;

        new Thread(this, this.t_name).start();
    }

    public void setChange_time(int time) {
        if(time > 0)
            this.change_time = time;
    }

    @Override
    public void run() {
        //Il tabacchino blocca le risorse condivise per prenderle tutte

        //Poi ne mette a disposizione solo alcune in quantita' limitate

        //Notifica a tutti i fumatori in attesa

        //Dopo change_time passati, o dopo che un fumatore ha finito di fumare,
        //il tabacchino cambia le risorse disponibili

        //Se una risorsa e' terminata lo notifica al monitor

    }
}
