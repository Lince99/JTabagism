import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;



public class Shop extends Thread {

    private Random randgen;
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private Semaphore lock_risorse;
    private String t_name;
    private int change_time;

    public Shop(ArrayList<Component> cmp, Semaphore s) {
        this.randgen = new Random();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = "Shop";
        this.change_time = 0;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
        System.out.println("\nSHOP CREATO:");
        this.printInfo();
    }

    public Shop(ArrayList<Component> cmp, String name, Semaphore s) {
        this.randgen = new Random();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = 0;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
        System.out.println("\nTABACCHINO CREATO:");
        this.printInfo();
    }

    public Shop(ArrayList<Component> cmp, String name, Semaphore s, int time) {
        this.randgen = new Random();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = time;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
        System.out.println("\nTABACCHINO CREATO:");
        this.printInfo();
    }

    public int getChange_time() {
        return this.change_time;
    }

    public void setChange_time(int time) {
        if(time > 0)
            this.change_time = time;
    }

    public void run() {
        int i = 0;
        int n_dispose = 0;

        while(true) {
            //Il tabacchino blocca le risorse condivise per prenderle tutte
            try {
                this.lock_risorse.acquire();
                //per ogni risorsa pubblica presente
                for(i = 0; i < this.public_resource.size(); i++) {
                    //ne estrae tutti i componenti in quella locale
                    publicToLocal(i);
                    //salva la posizione del componente senza scorte se presente
                    if(this.local_resource.get(i).getQuantity() == 0) {
                        //Se una risorsa e' terminata lo notifica al monitor
                        System.out.println("Risorsa "+
                                           this.local_resource.get(i).
                                                getType()+" vuota!");
                        Scanner scan = new Scanner(System.in);
                        System.out.println("Vuoi riempirla di quanto?");
                        n_dispose = scan.nextInt();
                        if(n_dispose <= 0) {
			    System.out.println("Uscita del tabacchino...");
                            return;
			}
                        else
                            this.local_resource.get(i).setQuantity(n_dispose);
                    }
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

            //Notifica a tutti i fumatori in attesa

            //Dopo change_time passati, o dopo che un fumatore ha finito
            //di fumare, il tabacchino cambia le risorse disponibili e ripete
            try {
                System.out.println("TABACCHINO DORMIRA' PER "+this.change_time+" ms");
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
        local.setQuantity(extracted.decreaseQuantity(extracted.getQuantity()));
        //alla fine assegna la copia all'array di risorse locali
        if(this.local_resource.size() <= i)
            this.local_resource.add(i, local);
        else
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
        local.decreaseQuantity(n_dispose_q);
        //per poi mettere quella quantita' in quella pubblica
        extracted.increaseQuantity(n_dispose_q);
        this.public_resource.set(i, extracted);
    }

    //estrae risorse dai componenti private per aggiungerli in quella pubblici
    public void printInfo() {
        System.out.println("Shop "+this.t_name+
                           " with clock set to: "+this.change_time);
        System.out.println("\nPublic resources ["+this.public_resource.size()+"]:");
        for(int i = 0; i < this.public_resource.size(); i++)
            System.out.print("\t"+this.public_resource.get(i).getType()+
                             ": "+this.public_resource.get(i).getQuantity());
        System.out.println("\nLocal resources ["+this.local_resource.size()+"]:");
        for(int i = 0; i < this.local_resource.size(); i++)
            System.out.print("\t"+this.local_resource.get(i).getType()+
                             ": "+this.local_resource.get(i).getQuantity());
        System.out.print("\n");
    }
}
