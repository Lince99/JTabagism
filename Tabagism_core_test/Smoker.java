import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;


public class Smoker extends Thread {

    private Random randgen;
    private Monitor monitor = new Monitor();
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private ArrayList<Integer> local_need_q;
    private Semaphore lock_risorse;
    private int smoke_time; //tempo di fumata
    private int min_smoke_time;
    private int min_smoke_q;
    private String t_name;
    private int n_smoke;

    public Smoker(ArrayList<Component> res, String name, int t, Semaphore sem) {
        this.randgen = new Random();
        this.public_resource = res;
        this.local_resource = new ArrayList<Component>(res.size());
        this.local_need_q = new ArrayList<Integer>();
        for(int i = 0; i < res.size(); i++)
        this.lock_risorse = sem;
        this.min_smoke_time = 1000; //Minimo un secondo di fumata
        this.smoke_time = randgen.nextInt(t)+this.min_smoke_time;
        this.t_name = name;
        //avvia il thread del fumatore
        new Thread(this, this.t_name).start();
    }

    @Override
    public void run() {
        int i;
        boolean need_public = false;
        int n_smoke = 3;

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
                //in vendita le risorse
                try {
                    this.lock_risorse.acquire();
                    //occupa le risorse e ne sottrae la quantita' necessaria
                    for(i = 0; i < this.public_resource.size(); i++)
                        publicToLocal(i);
                    System.out.print("smk PUBLIC\t");
                    monitor.printListInfo(public_resource);
                    System.out.print("Smoker "+this.t_name+"\t");
                    monitor.printListInfo(local_resource);
                } catch(InterruptedException mutex_e) {
                    mutex_e.printStackTrace();
                }
                this.lock_risorse.release();
                //REMOVE THIS
                try {
                    System.out.println("Smoker "+this.t_name+" dorme per "+
                                       this.smoke_time+" ms");
                    Thread.sleep(this.smoke_time);
                } catch(InterruptedException sleep_e) {
                    sleep_e.printStackTrace();
                }
            }
            //se ha tutte le risorse per fumare
            else if(hasAllLocal()) {
                //Fuma per tot tempo
                System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                System.out.println("SMOKER "+this.t_name+" FUMA PER "+
                                   this.smoke_time+" MS");
                System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                System.out.print("Smoker "+this.t_name+"\t");
                monitor.printListInfo(local_resource);
                smoke();
                n_smoke--;
                //Notifica al tabacchino che ha finito di fumare TODO
            }
        }
        System.out.println("\t\t\t\tFUMATORE "+this.t_name+" HA TERMINATO");
    }

    //estrae risorse dai componenti privati per aggiungerli in quelli pubblici
    private void publicToLocal(int i) {
        Component extracted;
        Component local;
        int min_extract = 1;
        int max_extract = 10;
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
        //crea un quantitativo random da estrarre
        rand_extract = this.randgen.nextInt(max_extract)+min_extract;
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
        int i;
        int rand_extract = 0;
        int min_smoke_q = 1;

        //riduce le risorse a random
        for(i = 0; i < this.local_resource.size(); i++) {
            usable = this.local_resource.get(i);
            if(usable.getQuantity() == min_smoke_q)
                rand_extract = min_smoke_q;
            else {
                rand_extract = this.randgen.nextInt(usable.getQuantity()-
                                                    min_smoke_q)+min_smoke_q;
            }
            usable.setQuantity(usable.getQuantity()-rand_extract);
        }
        try {
	        System.out.println("Fumatore dorme per "+this.smoke_time+"ms");
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
            if(this.local_resource.get(i).getQuantity() <= 0)
                return false;
        }

        //se ha tutto ritorna vero
        return true;
    }
}
