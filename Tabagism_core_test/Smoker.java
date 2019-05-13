import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Random;



public class Smoker extends Thread {
    private Random randgen = new Random();
    public Monitor monitor;
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private int max_local_q;
    private int min_smoke_q;
    private int max_smoke_q;
    private int smoke_time; //tempo di fumata
    private int min_smoke_time;
    private String t_name;
    private int n_smoke;

    private Semaphore lock_risorse;
    private volatile SharedVar wait_smoker;
    private Object lock;

    public Smoker(String name, ArrayList<Component> res, Semaphore sem,
                  Monitor monitor, int smoke_t, int n_smoke) {
        this.monitor = monitor;
        this.public_resource = res;
        this.local_resource = new ArrayList<Component>(res.size());
        this.max_local_q = 10;
        this.min_smoke_q = 1;
        this.max_smoke_q = 1;
        this.min_smoke_time = 1000; //Minimo un secondo di fumata
        if(smoke_t <= 0)
            smoke_t = this.min_smoke_time;
        this.smoke_time = randgen.nextInt(smoke_t)+this.min_smoke_time;
        this.t_name = name;
        this.n_smoke = n_smoke;
        this.lock_risorse = sem;
        this.wait_smoker = null;
        this.lock = null;
    }

    public Smoker(String name, ArrayList<Component> res, Semaphore sem,
                  Monitor monitor, int smoke_t, int n_smoke,
                  SharedVar wait, Object lock) {
        this.monitor = monitor;
        this.public_resource = res;
        this.local_resource = new ArrayList<Component>(res.size());
        this.max_local_q = 10;
        this.min_smoke_q = 1;
        this.max_smoke_q = 1;
        this.min_smoke_time = 1000; //Minimo un secondo di fumata
        if(smoke_t <= 0)
            smoke_t = this.min_smoke_time;
        this.smoke_time = randgen.nextInt(smoke_t)+this.min_smoke_time;
        this.t_name = name;
        this.n_smoke = n_smoke;
        this.lock_risorse = sem;
        this.wait_smoker = wait;
        this.lock = lock;
    }

    public int getMin_smoke_q() {
        return this.min_smoke_q;
    }
    public int getMax_smoke_q() {
        return this.max_smoke_q;
    }

    public void setMin_smoke_q(int q) {
        this.min_smoke_q = q;
    }
    public void setMax_smoke_q(int q) {
        this.max_smoke_q = q;
    }

    public int getMax_local_q() {
        return this.max_local_q;
    }
    public void setMax_local_q(int q) {
        this.max_local_q = q;
    }

    public void run() {
        int i = 0;
        boolean need_public = false;
        int shared = 0;

        //continua finche' non ha finito tutte le fumate
        while(n_smoke > 0) {
            synchronized(this.lock) {
                shared = try_getSharedState(2);
                //se il tabacchino invia il segnale di terminazione
                //segnale KILL
                if(shared == -1) {
                    this.monitor.printString("Uscita dello smoker "+
                                             this.t_name+"...");
                    return;
                }
            }
            //se il fumatore ha tutte le risorse necessarie a fumare lo fa
            if(hasAllLocal()) {
                //Fuma per tot tempo
                this.monitor.printString("\n- - - - - - - - - - - - - - - - - "+
                                         "- - - - - - - - - - - - - - - - -");
                this.monitor.printString("SMOKER "+this.t_name+" FUMA PER "+
                                   this.smoke_time+" MS");
                this.monitor.printString("- - - - - - - - - - - - - - - - - "+
                                         "- - - - - - - - - - - - - - - - -");
                //this.monitor.printInfo("Smoker "+this.t_name,
                //                       this.local_resource);
                smoke();
                n_smoke--;
            }
            else {
                synchronized(this.lock) {
                    //Il fumatore attende che il tabacchino venda risorse
                    //segnale END (attende per altri segnali)
                    shared = try_getSharedState(2);
                    //segnale KILL
                    if(getSharedState() == -1) {
                        this.monitor.printString("Uscita dello smoker "+
                                                 this.t_name);
                        return;
                    }
                    this.monitor.printString("SMOKER "+this.t_name+
                                             " VEDE: "+shared);
                    //this.monitor.printInfo("smoker "+this.t_name,
                    //                       this.local_resource);
                    //segnali ACK e CHANGE
                    if(shared == 0 || shared == 1) {
                        //occupa le risorse e ne sottrae la quantita' necessaria
                        try {
                            this.lock_risorse.acquire();
                            for(i = 0; i < this.public_resource.size(); i++)
                                publicToLocal(i);
                        } catch(InterruptedException mutex_e) {
                            mutex_e.printStackTrace();
                        }
                        this.lock_risorse.release();
                        //se non possiede tutte le risorse lo notifica
                        if(!hasAllLocal()) {
                            if(getSharedState() == -1) {
                                this.monitor.printString("Uscita dello smoker "+
                                                         this.t_name);
                                return;
                            }
                            try_setSharedState(1, 2);
                        }
                    }
                }
            }
        }

        synchronized(this.lock) {
            //TODO USANDO GETSHARED FUNZIONA, senza VA IN STARVATION
            this.monitor.printString("\t\t\t\t- - - FUMATORE\t"+this.t_name+
                               "\tHA TERMINATO ("+getSharedState()+")- - -");
            //notifica al tabacchino che ha finito di fumare
            //segnale END
            this.monitor.printString("Smoker "+this.t_name+
                                     " legge "+getSharedState());
            try_setSharedState(2, 2);
            this.monitor.printString("Smoker "+this.t_name+
                                     " notifica terminazione!");
        }
    }

    //estrae risorse dai componenti privati per aggiungerli in quelli pubblici
    private void publicToLocal(int i) {
        Component extracted;
        Component local;
        int min_extract = 1;
        int max_extract = 5;
        int rand_extract = 0;
        int local_current = 0;

        //ne legge le proprieta'
        extracted = this.public_resource.get(i);
        //crea una copia locale
        local = new Component(extracted.getType());
        //ottiene il quantitativo della risorsa locale
        if(this.local_resource.size() <= i)
            local_current = 0;
        else
            local_current = this.local_resource.get(i).getQuantity();
        //se ha gia' abbastanza risorse locali esce dall'estrazione
        if(local_current >= this.max_local_q)
            return;
        //crea un quantitativo random da estrarre
        rand_extract = this.randgen.nextInt(max_extract-min_extract)+
                       min_extract;
        //e estrae il contenuto da quella pubblica in quella locale
        local.setQuantity(local_current+
                          extracted.decreaseQuantity(rand_extract));
        //alla fine assegna la copia all'array di risorse locali
        if(this.local_resource.size() <= i)
            this.local_resource.add(i, local);
        else
            this.local_resource.set(i, local);
    }

    //azione che viene avviata solo se il fumatore possiede tutte le risorse
    private void smoke() {
        Component usable;
        int usable_q = 0;
        int i = 0;
        int rand_extract = 0;

        //riduce le risorse a random
        for(i = 0; i < this.local_resource.size(); i++) {
            usable = this.local_resource.get(i);
            usable_q = usable.getQuantity();
            //estrae una quantita' random rispettando il minimo e il massimo
            if(usable_q == this.min_smoke_q)
                rand_extract = this.min_smoke_q;
            else {
                if(usable_q < this.max_smoke_q)
                    this.max_smoke_q = usable_q;
                if(this.max_smoke_q == this.min_smoke_q)
                    rand_extract = this.max_smoke_q;
                else {
                    rand_extract = this.randgen.nextInt(this.max_smoke_q-
                                                        this.min_smoke_q)+
                                   this.min_smoke_q;
                }
            }
            usable.setQuantity(usable_q - rand_extract);
        }
        //fuma per tot tempo
        try {
            Thread.sleep(this.smoke_time);
        } catch(InterruptedException sleep_e) {
            sleep_e.printStackTrace();
        }
    }

    //se non ha tutte le risorse locali per fumare ritorna falso
    private boolean hasAllLocal() {
        int size = this.local_resource.size();

        if(size <= 0)
            return false;
        for(int i = 0; i < size; i++) {
            if(this.local_resource.get(i).getQuantity() <= 0 ||
               this.local_resource.get(i).getQuantity() < this.min_smoke_q)
                return false;
        }

        //se ha tutto ritorna vero
        return true;
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
                if(this.wait_smoker.get() != not_overwrite) {
                    this.wait_smoker.set(val);
                    this.lock.notifyAll();
                    done = true;
                }
                else {
                    this.lock.notifyAll();
                    try {
                        this.monitor.printString("Smoker "+this.t_name+" va in attesa... ("+this.wait_smoker.get()+")");
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
