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
    private SharedVar wait_smoker;
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
        int i;
        boolean need_public = false;

        //continua finche' non ha finito tutte le fumate
        while(n_smoke > 0) {
            //Il fumatore controlla le risorse locali per vedere se possiede
            //quelle necessarie a fumare
            if(this.local_resource.size() == 0) {
                need_public = true;
            }
            for(i = 0; i < this.local_resource.size(); i++) {
                if(this.local_resource.get(i).getQuantity() == 0) {
                    System.out.println("Smoker "+this.t_name+" non ha "+
                                       this.local_resource.get(i).getType());
                    need_public = true;
                    break;
                }
                else {
                    System.out.println("Smoker "+this.t_name+" possiede "+i);
                    need_public = false;
                }
            }
            if(need_public) {
                //Il fumatore attende che il tabacchino metta
                //in vendita alcune risorse
                if(this.lock != null) {
                    synchronized(this.lock) {
                        //se il tabacchino invia il segnale di terminazione
                        if(this.wait_smoker.get() == -1) {
                            System.out.println("Smoker "+this.t_name+
                                               " TERMINATO CON -1!");
                            return;
                        }
                        //il fumatore attende nel caso non ci siano stati cambi
                        while(this.wait_smoker.get() > 0) {
                            try {
                                System.out.println("Smoker "+this.t_name+
                                                   " attende... ("+
                                                   this.wait_smoker.get()+")");
                                lock.wait();
                            } catch(InterruptedException wait_e) {
                                wait_e.printStackTrace();
                            }
                        }
                    }
                }
                try {
                    this.lock_risorse.acquire();
                    //occupa le risorse e ne sottrae la quantita' necessaria
                    for(i = 0; i < this.public_resource.size(); i++)
                        publicToLocal(i);
                } catch(InterruptedException mutex_e) {
                    mutex_e.printStackTrace();
                }
                this.lock_risorse.release();
            }
            //se ha tutte le risorse per fumare
            else if(hasAllLocal()) {
                //Fuma per tot tempo
                System.out.println("\n- - - - - - - - - - - - - - - - - - - "+
                                   "- - - - - - - - - - - - - - - - - - - - ");
                System.out.println("SMOKER "+this.t_name+" FUMA PER "+
                                   this.smoke_time+" MS");
                System.out.println("\n- - - - - - - - - - - - - - - - - - - "+
                                   "- - - - - - - - - - - - - - - - - - - - ");
                smoke();
                n_smoke--;
                if(this.lock != null) {
                    //notifica al tabacchino che ha finito di fumare
                    synchronized(this.lock) {
                        if(this.wait_smoker.get() >= 0) {
                            System.out.println("Smoker "+this.t_name+
                                               " notifica! ("+
                                               this.wait_smoker.get()+")");
                            this.wait_smoker.set(1);
                            this.lock.notifyAll();
                        }
                    }
                }
                try {
                    System.out.println("Fumatore "+this.t_name+
                                       " dorme per "+this.smoke_time+"ms");
                    Thread.sleep(this.smoke_time);
                } catch(InterruptedException sleep_e) {
                    sleep_e.printStackTrace();
                }
            }
        }
        System.out.println("\t\t\t\t- - - FUMATORE "+this.t_name+
                           " HA TERMINATO - - -");
        if(this.lock != null) {
            //notifica al tabacchino che ha finito di fumare
            synchronized(this.lock) {
                if(this.wait_smoker.get() >= 0) {
                    System.out.println("Smoker "+this.t_name+
                                       " notifica terminazione! ("+
                                       this.wait_smoker.get()+")");
                    while(this.wait_smoker.get() == 2) {
                        try {
                            this.lock.wait();
                        } catch(InterruptedException sleep_e) {
                            sleep_e.printStackTrace();
                        }
                    }
                    this.wait_smoker.set(2);
                    this.lock.notifyAll();
                }
            }
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
}
