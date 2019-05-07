import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.Random;


public class Smoker extends Thread {

    private Random randgen;
    private ArrayList<Component> public_resource;
    private ArrayList<Component> local_resource;
    private Semaphore lock_risorse;
    private int smoke_time; //tempo di fumata
    private int min_smoke_time;
    private int min_smoke_q;
    private String t_name;

    public Smoker(ArrayList<Component> resource, String name, int max_t, Semaphore sem) {
        this.randgen = new Random();
        this.public_resource = resource;
        this.local_resource = new ArrayList<Component>(resource.size());
        this.lock_risorse = sem;
        this.min_smoke_time = 1000; //Minimo un secondo di fumata
        this.smoke_time = randgen.nextInt(max_t)+this.min_smoke_time;
        this.t_name = name;
        //avvia il thread del fumatore
        new Thread(this, this.t_name).start();
        System.out.println("\nFUMATORE CREATO:");
        this.printInfo();
    }

    @Override
    public void run() {
        int i;
        boolean need_public = false;

        //Il fumatore controlla le risorse locali per vedere se possiede
        //quelle necessarie a fumare
        for(i = 0; i < this.local_resource.size(); i++) {
            if(this.local_resource.get(i).getQuantity() == 0) {
                need_public = true;
                break;
            }
        }

        if(need_public) {
            //Il fumatore attende che il tabacchino metta in vendita delle risorse
            try {
                this.lock_risorse.acquire();
                //occupa le risorse e ne sottrae la quantita' necessaria
                for(i = 0; i < this.local_resource.size(); i++)
                    publicToLocal(i);
            } catch(InterruptedException mutex_e) {
                mutex_e.printStackTrace();
            }
            this.lock_risorse.release();
        }
        else {
            //Fuma per tot tempo
            smoke();

            //Notifica al tabacchino che ha finito di fumare
        }
    }

    //estrae risorse dai componenti privati per aggiungerli in quelli pubblici
    private void publicToLocal(int i) {
        Component extracted;
        Component local;
        int min_extract = 10;
        int max_extract = 100;
        int rand_extract = 0;

        //ne legge le proprieta'
        extracted = this.public_resource.get(i);
        //crea una copia locale
        local = new Component(extracted.getType());
        //e estrae il contenuto da quella pubblica in quella locale
        rand_extract = this.randgen.nextInt(max_extract)+min_extract;
        local.setQuantity(extracted.decreaseQuantity(rand_extract));
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

        //riduce le risorse a random
        for(i = 0; i < this.local_resource.size(); i++) {
            usable = this.local_resource.get(i);
            rand_extract = this.randgen.nextInt(
                            usable.getQuantity()-min_smoke_q)+min_smoke_q;
            usable.setQuantity(usable.getQuantity()-rand_extract);
        }
        System.out.println("FUMATORE "+this.t_name+" FUMA:");
        this.printInfo();
        try {
	    System.out.println("Fumatore dorme per "+this.smoke_time+"ms");
            Thread.sleep(this.smoke_time);
        } catch(InterruptedException sleep_e) {
            sleep_e.printStackTrace();
        }
    }

    public void printInfo() {
        System.out.println("Smoker "+this.t_name+
                           " with smoking time set to: "+this.smoke_time);
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
