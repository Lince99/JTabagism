package Tabagism.Tabagism_core;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;



public class Smoker extends Thread {

    ArrayList public_resource;
    private ArrayList local_resource;
    private int smoke_time;

    public Smoker(ArrayList resource) {
        this.public_resource = resource;
        this.local_resource = new ArrayList;
        this.smoke_time = 0;
    }

    @Override
    public void run() {
        //Il fumatore attende che il tabacchino metta in vendita delle risorse
        wait();
        //Il fumatore entra nelle risorse disponibili per vedere se sono quelle
        //necessarie a fumare

        //Occupa le risorse e ne sottrae la quantita' necessaria

        //Fuma per tot tempo

        //Notifica al tabacchino che ha finito di fumare
    }

    private void smoke() {

    }
}
