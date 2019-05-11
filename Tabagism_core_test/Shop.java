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
    private volatile SharedVar wait_smoker;
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
        int round = 0; //numero di fumatori che hanno terminato
        int shared = 0;

        //aggiunge un ritardo per non esagerare con i check
        if(this.change_time == 0)
            this.change_time = 100;

        //il tabacchino continua finchè tutti i fumatori non hanno finito
        while(round < this.max_round) {
            //Notifica a tutti i fumatori in attesa
            shared = getSharedState();
            //segnale KILL
            if(shared == -1) {
                this.monitor.printString("Uscita dello shop "+this.t_name);
                return;
            }
            //nessun fumatore ha modificato la variabile condivisa
            //segnale ACK
            else if(shared == 0) {
                this.monitor.printString("Shop "+this.t_name+
                                   " attende modifiche per "+
                                   this.change_time+" ms"+"\t("+round+")");
                synchronized(this.lock) {
                    try {
                        this.lock.wait();
                    } catch(InterruptedException sleep_e) {
                        sleep_e.printStackTrace();
                    }
                }
                /*try {
                    Thread.sleep(this.change_time);
                } catch(InterruptedException sleep_e) {
                    sleep_e.printStackTrace();
                }*/
            }
            //almeno un fumatore ha notificato la necessita' di cambio
            //segnale CHANGE
            else if(shared == 1) {
                //emette segnale ACK
                if(changeResources(round)) {
                    this.monitor.printString("Shop "+this.t_name+" notifica!");
                    setSharedState(0);
                }
                //emette segnale KILL
                else {
                    this.monitor.printString("Shop "+this.t_name+
                                       " TERMINA I FUMATORI!");
                }
            }
            //un fumatore ha terminato di fumare
            //segnale END
            else if(shared == 2) {
                round++;
                //emette segnale ACK
                this.monitor.printString(this.t_name+
                                   ": Un fumatore ha finito!");
                setSharedState(0);
            }
        }
    }

    //azione principale del tabacchino
    private boolean changeResources(int round) {
        Scanner scan = new Scanner(System.in);
        int n_dispose = 0;
        int i = 0;

        try {
            //il tabacchino blocca le risorse condivise per prenderle tutte
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
                        this.monitor.printString("Shop "+this.t_name+
                                           " fa attendere gli smoker...");
                        if(getSharedState() == 2) {
                            round++;
                            //emette segnale ACK
                            this.monitor.printString(this.t_name+
                                               ": Un fumatore ha finito!");
                        }
                        setSharedState(1);
                        //Se una risorsa e' terminata lo notifica al monitor
                        this.monitor.printString("Risorsa "+
                                           this.local_resource.get(i).
                                           getType()+" vuota!");
                        this.monitor.printString("Vuoi riempirla di quanto?");
                        n_dispose = scan.nextInt();
                        //termina i fumatori
                        if(n_dispose <= 0) {
                            setSharedState(-1);
                            return false;
                        }
                        //aumenta la dimensione massima
                        this.local_resource.get(i).setQuantity(n_dispose);
                        this.monitor.setMax_size_elem(i, n_dispose);
                        //il tabacchino aggiunge risorse e risveglia i fumatori
                        this.monitor.printString("Shop "+this.t_name+
                                           " fa ripartire i fumatori!");
                        setSharedState(0);
                    }
                }
            }
            if(round <= 1 && getSharedState() == 1) {
                this.monitor.printInfo(this.t_name+" local (PUBLIC to LOCAL)",
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
            if(round <= 1 && getSharedState() == 1) {
                this.monitor.printInfo(this.t_name+" local (LOCAL to PUBLIC)",
                                  this.local_resource);
                this.monitor.printInfo(this.t_name+" PUBLIC",
                                  this.public_resource);
            }
            System.out.print("\n");
        } catch(InterruptedException lock_e) {
            lock_e.printStackTrace();
        }
        this.lock_risorse.release();

        return true;
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

    private void setSharedState(int val) {
        synchronized(this.lock) {
            this.wait_smoker.set(val);
            this.lock.notifyAll();
        }
    }

    private void try_setSharedState(int val, int not_overwrite) {
        boolean done;

        do {
            done = false;
            synchronized(this.lock) {
                this.wait_smoker.get();
                if(this.wait_smoker.get() != not_overwrite) {
                    this.wait_smoker.set(val);
                    this.lock.notifyAll();
                    done = true;
                }
                else {
                    try {
                        this.lock.wait();
                    } catch(InterruptedException wait_e) {
                        wait_e.printStackTrace();
                    }
                }
            }
        } while(!done);
    }

    private int try_getSharedState(int not_overwrite) {
        int val = -1;

        do {
            synchronized(this.lock) {
                val = this.wait_smoker.get();
                if(this.wait_smoker.get() != not_overwrite)
                    return val;
                else {
                    try {
                        this.lock.wait();
                    } catch(InterruptedException wait_e) {
                        wait_e.printStackTrace();
                    }
                }
            }
        } while(val == not_overwrite);

        return -1;
    }

    private int getSharedState() {
        int val = 0;

        synchronized(this.lock) {
            val = this.wait_smoker.get();
        }

        return val;
    }
}
