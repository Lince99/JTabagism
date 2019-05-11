import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;



public class Shop extends Thread {
    private Random randgen = new Random();
    public Monitor monitor;
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private String t_name;
    private int change_time;
    private int max_round;

    private Semaphore lock_risorse;
    private SharedVar wait_smoker;
    private Object lock;

    private int gen_last_chose = -1;

    public Shop(ArrayList<Component> cmp, Semaphore s, Monitor monitor) {
        this.monitor = monitor;
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = "Shop";
        this.change_time = 1000;
        this.max_round = 50;
        this.wait_smoker = null;
        this.lock = null;
    }

    public Shop(String name, ArrayList<Component> cmp,
                Semaphore s, Monitor monitor) {
        this.monitor = monitor;
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = 0;
        this.max_round = 50;
        this.wait_smoker = null;
        this.lock = null;
    }

    public Shop(String name, ArrayList<Component> cmp, Semaphore s,
                Monitor monitor, SharedVar wait, Object lock) {
        this.monitor = monitor;
        this.public_resource = cmp;
        this.local_resource = new ArrayList<Component>(cmp.size());
        this.lock_risorse = s;
        this.t_name = name;
        this.change_time = 0;
        this.max_round = 50;
        this.wait_smoker = wait;
        this.lock = lock;
    }

    public int getChange_time() {
        return this.change_time;
    }

    public void setChange_time(int time) {
        if(time >= 0)
            this.change_time = time;
    }

    public int getMax_round() {
        return this.max_round;
    }

    public void setMax_round(int r) {
        if(r >= 0)
            this.max_round = r;
    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        int i = 0;
        int n_dispose = 0;
        int round = 0; //numero di fumatori che hanno terminato

        //aggiunge un ritardo per non esagerare con i check
        if(this.change_time == 0)
            this.change_time = 100;

        //il tabacchino continua finchè tutti i fumatori non hanno finito
        while(round < this.max_round) {
            if(this.lock != null) {
                //Notifica a tutti i fumatori in attesa
                synchronized(this.lock) {
                    System.out.println("\t\tWAIT SMOKER = "+
                                       this.wait_smoker.get()+
                                       "\t\tround = "+round);
                    //nessun fumatore ha modificato la variabile condivisa
                    if(this.wait_smoker.get() == 0) {
                        System.out.println("Shop "+this.t_name+
                                           " attende modifiche per "+
                                           this.change_time+" ms");
                        try {
                            Thread.sleep(this.change_time);
                        } catch(InterruptedException sleep_e) {
                            sleep_e.printStackTrace();
                        }
                    }
                    //almeno un fumatore ha notificato la necessita' di cambio
                    else if(this.wait_smoker.get() == 1) {
                        this.wait_smoker.set(0);
                        System.out.println("Shop "+this.t_name+" notifica!");
                        this.lock.notifyAll();
                    }
                    //un fumatore ha terminato di fumare
                    else if(this.wait_smoker.get() == 2){
                        System.out.println(this.t_name+
                                           ": Un fumatore ha finito!");
                        this.wait_smoker.set(0);
                        this.lock.notifyAll();
                        round++;
                        continue;
                    }
                }
            }
            //senza sync va a tempo
            else {
                //Dopo change_time passati il tabacchino
                //cambia le risorse disponibili e ripete
                try {
                    System.out.println("Shop "+this.t_name+
                                       " dorme per "+this.change_time+" ms");
                    Thread.sleep(this.change_time);
                } catch(InterruptedException sleep_e) {
                    sleep_e.printStackTrace();
                }
            }
            //il tabacchino blocca le risorse condivise per prenderle tutte
            try {
                this.lock_risorse.acquire();
                //per ogni risorsa pubblica presente
                for(i = 0; i < this.public_resource.size(); i++) {
                    //ne estrae tutti i componenti in quella locale
                    publicToLocal(i);
                    //salva la posizione del componente senza scorte, se presente
                    if(this.local_resource.get(i).getQuantity() == 0) {
                        //mette in pausa i fumatori
                        synchronized(this.lock) {
                            //setta a 1 poiche' e' richiesto un cambio
                            this.wait_smoker.set(1);
                            System.out.println("Shop "+this.t_name+
                                               " fa attendere gli smoker...");
                            this.lock.notifyAll();
                            //Se una risorsa e' terminata lo notifica al monitor
                            System.out.println("Risorsa "+
                                               this.local_resource.get(i).
                                               getType()+" vuota!");
                            System.out.println("Vuoi riempirla di quanto?");
                            n_dispose = scan.nextInt();
                            if(n_dispose <= 0) {
                                System.out.println("Uscita dello shop...");
                                //termina i fumatori
                                this.wait_smoker.set(-1);
                                System.out.println("Shop "+this.t_name+
                                                   " TERMINA I FUMATORI!");
                                this.lock.notifyAll();
                                return;
                            }
                            //aumenta la dimensione massima
                            else {
                                this.local_resource.get(i).
                                                    setQuantity(n_dispose);
                                monitor.setMax_size_elem(i, n_dispose);
                            }
                            //il tabacchino ha aggiunto risorse, risveglia i fumatori
                            this.wait_smoker.set(0);
                            System.out.println("Shop "+this.t_name+
                                               " fa ripartire i fumatori!");
                            this.lock.notifyAll();
                        }
                    }
                }
                if(round <= 1) {
                    monitor.printInfo(this.t_name+" local (PUBLIC to LOCAL)",
                                      this.local_resource);
                    System.out.print("\n");
                }

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
                if(round <= 1) {
                    monitor.printInfo(this.t_name+" PUBLIC",
                                      this.public_resource);
                    monitor.printInfo(this.t_name+" local (LOCAL to PUBLIC)",
                                      this.local_resource);
                }
                System.out.print("\n");
            } catch(InterruptedException mutex_e) {
                mutex_e.printStackTrace();
            }
            this.lock_risorse.release();
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
        Component extracted = null;
        Component local = null;
        int n_dispose_q = 0;
        int n_rand = 0;

        //ottiene la risorsa locale
        local = this.local_resource.get(i);
        //e quella pubblica da riempire
        extracted = this.public_resource.get(i);
        //estrae un quantitativo casuale dalla risorsa locale
        while(n_rand == this.gen_last_chose) {
            n_rand = this.randgen.nextInt(local.getQuantity())+1;
        }
        this.gen_last_chose = n_rand;
        n_dispose_q = n_rand;
        local.decreaseQuantity(n_dispose_q);
        //per poi mettere quella quantita' in quella pubblica
        extracted.increaseQuantity(n_dispose_q);
        this.public_resource.set(i, extracted);
    }
}
