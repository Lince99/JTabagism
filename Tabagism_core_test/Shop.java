import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;



public class Shop extends Thread {

    private Random randgen;
    private Monitor monitor;
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private Semaphore lock_risorse;
    private String t_name;
    private int change_time;

    private AtomicBoolean wait_smoker;
    private Object lock;

    public Shop(ArrayList<Component> cmp, Semaphore s) {
        this.randgen = new Random();
        this.monitor = new Monitor();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = "Shop";
        this.change_time = 1000;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
    }

    public Shop(ArrayList<Component> cmp, String name, Semaphore s) {
        this.randgen = new Random();
        this.monitor = new Monitor();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = 1000;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
    }

    public Shop(ArrayList<Component> cmp, String name, Semaphore s, int time) {
        this.randgen = new Random();
        this.monitor = new Monitor();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = time;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
    }

    public Shop(ArrayList<Component> cmp, String name, Semaphore s, int time,
                AtomicBoolean wait, Object lock) {
        this.randgen = new Random();
        this.monitor = new Monitor();
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = time;
        this.wait_smoker = wait;
        this.lock = lock;
        //avvia il thread del tabacchino
        new Thread(this, this.t_name).start();
    }

    public int getChange_time() {
        return this.change_time;
    }

    public void setChange_time(int time) {
        if(time > 0)
            this.change_time = time;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);
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
                                           this.local_resource.get(i).getType()+
                                           " vuota!");
                        System.out.println("Vuoi riempirla di quanto?");
                        n_dispose = scan.nextInt();
                        if(n_dispose <= 0) {
            			    System.out.println("Uscita dello shop...");
                            return;
            			}
                        else
                            this.local_resource.get(i).setQuantity(n_dispose);
                    }
                }
                System.out.print("PUBLIC\t\t");
                monitor.printListInfo(public_resource);
                System.out.print("Shop(P->L)\t");
                monitor.printListInfo(local_resource);
                System.out.print("\n");

                //Poi ne mette a disposizione solo alcune in quantita' limitate
                n_dispose = 0;
                for(i = 0; i < this.local_resource.size() &&
                    n_dispose < this.local_resource.size(); i++) {
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
                System.out.print("PUBLIC\t\t");
                monitor.printListInfo(public_resource);
                System.out.print("Shop(L->P)\t");
                monitor.printListInfo(local_resource);
                System.out.print("\n");
            } catch(InterruptedException mutex_e) {
                mutex_e.printStackTrace();
            }
            this.lock_risorse.release();

            //Dopo change_time passati, o dopo che un fumatore ha finito
            //di fumare, il tabacchino cambia le risorse disponibili e ripete
            try {
                System.out.println("Shop "+this.t_name+" dorme per "+
                                   this.change_time+" ms");
                Thread.sleep(this.change_time);
            } catch(InterruptedException sleep_e) {
                sleep_e.printStackTrace();
            }
            //Notifica a tutti i fumatori in attesa
            /*synchronized(this.lock) {
                try {
                    //prima attende che almeno un fumatore abbia finito
                    while(this.wait_smoker.get()) {
                        this.lock.wait();
                        System.out.println("Shop "+this.t_name+" attende...");
                    }
                    this.wait_smoker.set(false);
                    System.out.println("Shop "+this.t_name+" notifica!");
                    this.lock.notifyAll();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    //estrae risorse dai componenti privati per aggiungerli in quelli pubblici
    private void publicToLocal(int i) {
        Component extracted;
        Component local;
        int local_current = 0;
        int public_current = 0;

        //ne legge le proprieta'
        extracted = this.public_resource.get(i);
        //crea una copia locale
        local = new Component(extracted.getType());
        //ottiene il quantitativo della risorsa locale
        if(this.local_resource.size() <= i)
            local_current = 0;
        else
            local_current = this.local_resource.get(i).getQuantity();
        //ottiene il quantitativo della risorsa pubblica
        public_current = extracted.getQuantity();
        //e estrae tutto il contenuto da quella pubblica in quella locale
        local.setQuantity(local_current+
                          extracted.decreaseQuantity(public_current));
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
}
