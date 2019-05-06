import java.util.ArrayList;
import java.util.concurrent.Semaphore;



public class Main {

    private static int n_fumatori;
    private static int max_smoke_time;
    public static ArrayList<Component> risorse;

    public static void main(String[] args) {
        int tabacco_q = 10000;
        int cartina_q = 1000;
        int filtro_q = 1000;
        int accendino_q = 100;
        int check_q_tbc = 10000; //ogni 10 secondi controlla le risorse
        Shop tabacchino;
        ArrayList<Smoker> fumatori;
        Semaphore lock_risorse = new Semaphore(1);

        //Setup iniziale
        n_fumatori = 4;
        max_smoke_time = 10000;
        //Crea le risorse per i fumatori e il tabacchino
        risorse = new ArrayList<Component>();
        risorse.add(new Component("Tabacco", tabacco_q));
        risorse.add(new Component("Cartina", cartina_q));
        risorse.add(new Component("Filtro", filtro_q));
        risorse.add(new Component("Accendino", accendino_q));

        //Avvia il monitor

        //Crea il thread del tabacchino
        tabacchino = new Shop(risorse, "tbc", lock_risorse, check_q_tbc);
        //tabacchino.printInfo();
        //attende che il tabacchino inizi ad occupare le risorse
        try {
            Thread.sleep(100);
        } catch(InterruptedException sleep_e) {
            sleep_e.printStackTrace();
        }

        //Crea i thread dei fumatori
        fumatori = new ArrayList<Smoker>();
        for(int i = 0; i < n_fumatori; i++) {
            fumatori.add(new Smoker(risorse, "smc#"+i, max_smoke_time, lock_risorse));
            //fumatori.get(i).printInfo();
        }

    }
}
