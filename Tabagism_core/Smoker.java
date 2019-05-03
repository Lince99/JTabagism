package Tabagism.Tabagism_core;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;


public class Smoker extends Thread {

    ArrayList public_resource;
    private ArrayList local_resource;
    //tempo di fumata
    private int smoke_time;
    private int min_smoke_time;
    private Random randgen;

    public Smoker(ArrayList resource, int max_smoke_t) {
        this.public_resource = resource;
        this.local_resource = new ArrayList();
        this.randgen = new Random();
        this.min_smoke_time = 1000; //Minimo un secondo di fumata
        this.smoke_time = randgen.nextInt(max_smoke_t)+this.min_smoke_time;
    }

    @Override
    public void run() {
        //Il fumatore attende che il tabacchino metta in vendita delle risorse
        try {
            wait();
        } catch(InterruptedException in_e) {
            
        }
        //Il fumatore entra nelle risorse disponibili per vedere se sono quelle
        //necessarie a fumare

        //Occupa le risorse e ne sottrae la quantita' necessaria

        //Fuma per tot tempo

        //Notifica al tabacchino che ha finito di fumare
    }

    //Azione che viene avviata solo se il fumatore possiede tutte le risorse
    private void smoke() {

    }
}
