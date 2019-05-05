package Tabagism.Tabagism_core;

import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;



public class Shop extends Thread {

    private Random randgen;
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private Semaphore lock_risorse;
    private String t_name;
    private int change_time;

    public Shop(ArrayList<Component> components, String name, Semaphore sem) {
        this.randgen = new Random();
        this.public_resource = components;
        this.local_resource = new ArrayList<Component>(components.size());
        this.lock_risorse = sem;
        this.t_name = name;
        this.change_time = 0;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
    }

    public void setChange_time(int time) {
        if(time > 0)
            this.change_time = time;
    }

    public void run() {
        boolean resource_empty = false;
        int resource_empty_pos = 0;
        int i = 0;
        int n_dispose = 0;

        while(true) {
            //Il tabacchino blocca le risorse condivise per prenderle tutte
            try {
                this.lock_risorse.acquire();
                //per ogni risorsa pubblica presente
                for(i = 0; i < this.local_resource.size(); i++) {
                    //ne estrae tutti i componenti in quella locale
                    publicToLocal(i);
                    //salva la posizione del componente senza scorte se presente
                    if(this.local_resource.get(i).getQuantity() == 0) {
                        resource_empty = true;
                        resource_empty_pos = i;
                        break;
                    }
                    else
                        resource_empty = true;
                }

                //Poi ne mette a disposizione solo alcune in quantita' limitate
                n_dispose = 0;
                for(i = 0; i < this.local_resource.size(); i++) {
                    //le sceglie a caso
                    if(randgen.nextBoolean()) {
                        n_dispose++;
                        localToPublic(i);
                    }
                }
                //se non ha effettuato alcuna modifica, ne effettua almeno una
                if(n_dispose == 0) {
                    i = randgen.nextInt(this.local_resource.size()-1);
                    localToPublic(i);
                }

            } catch(InterruptedException mutex_e) {
                mutex_e.printStackTrace();
            }
            this.lock_risorse.release();

            //Se una risorsa e' terminata lo notifica al monitor
            if(resource_empty) {
                System.out.println("Risorsa "+
                                   this.local_resource.
                                        get(resource_empty_pos).
                                        getType()+" vuota!");
            }

            //Notifica a tutti i fumatori in attesa

            //Dopo change_time passati, o dopo che un fumatore ha finito di fumare,
            //il tabacchino cambia le risorse disponibili e ripete
            try {
                Thread.sleep(this.change_time);
            } catch(InterruptedException sleep_e) {
                sleep_e.printStackTrace();
            }
        }
    }

    //estrae risorse dai componenti privati per aggiungerli in quelli pubblici
    private void publicToLocal(int i) {
        Component extracted;
        Component local;

        //ne legge le proprieta'
        extracted = this.public_resource.get(i);
        //crea una copia locale
        local = new Component(extracted.getType());
        //e estrae il contenuto da quella pubblica in quella locale
        local.setQuantity(extracted.reduceQuantity(extracted.getQuantity()));
        //alla fine assegna la copia all'array di risorse locali
        this.local_resource.set(i, local);
    }

    //estrae risorse dai componenti pubblici per aggiungerli in quelli privati
    private void localToPublic(int i) {
        Component extracted;
        Component local;
        int n_dispose_q = 0;

        //ottiene la risorsa locale
        local = this.local_resource.get(i);
        //e quella pubblica da riempire
        extracted = this.public_resource.get(i);
        //estrae un quantitativo casuale dalla risorsa locale
        n_dispose_q = this.randgen.nextInt(local.getQuantity())+1;
        local.reduceQuantity(n_dispose_q);
        //per poi mettere quella quantita' in quella pubblica
        extracted.increaseQuantity(n_dispose_q);
        this.public_resource.set(i, extracted);
    }

    //estrae risorse dai componenti private per aggiungerli in quella pubblici
    public void printInfo() {
        System.out.println("Shop "+this.t_name+
                           " with clock set to: "+this.change_time);
        System.out.println("\tPublic resources: "+this.public_resource);
        System.out.println("\tLocal resources: "+this.local_resource);
    }
}
