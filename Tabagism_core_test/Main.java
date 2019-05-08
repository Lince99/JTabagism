import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Scanner;



public class Main {

    //risorse pubbliche
    public static ArrayList<Component> risorse;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int tabacco_q = 100;
        int cartina_q = 30;
        int filtro_q = 5;
        int accendino_q = 8;
        Shop tabacchino;
        int check_q_tbc = 4000; //ogni n secondi controlla le risorse
        //AtomicBoolean sync_shop_smoker = new AtomicBoolean(false);
        //Object lock_sync = new Object();
        ArrayList<Smoker> fumatori;
        int n_fumatori = 0;
        int max_smoke_time = 0;
        Semaphore lock_risorse = new Semaphore(1);

        //Setup iniziale
        System.out.println("Quanto tabacco vuoi creare?");
        tabacco_q = scan.nextInt();
        System.out.println("Quante cartine vuoi creare?");
        cartina_q = scan.nextInt();
        System.out.println("Quanti filtri vuoi creare?");
        filtro_q = scan.nextInt();
        System.out.println("Quanti accendini vuoi creare?");
        accendino_q = scan.nextInt();
        System.out.println("Ogni quanti secondi il tabacchino cambia vendite?");
        check_q_tbc = scan.nextInt()*1000;
        System.out.println("Quanti fumatori ci sono?");
        n_fumatori = scan.nextInt();
        System.out.println("Quanti secondi fumano al massimo?");
        max_smoke_time = scan.nextInt();
        //Crea le risorse per i fumatori e il tabacchino
        risorse = new ArrayList<Component>();
        risorse.add(new Component("Tabacco", tabacco_q));
        risorse.add(new Component("Cartina", cartina_q));
        risorse.add(new Component("Filtro", filtro_q));
        risorse.add(new Component("Accendino", accendino_q));

        //Crea il thread del tabacchino
        System.out.println("- - - Creazione tabacchino... - - -");
        //tabacchino = new Shop(risorse, "tbc", lock_risorse, check_q_tbc,
        //                      sync_shop_smoker, lock_sync);
        tabacchino = new Shop(risorse, "tbc", lock_risorse, check_q_tbc);
        //attende che il tabacchino inizi ad occupare le risorse
        try {
            Thread.sleep(100);
        } catch(InterruptedException sleep_e) {
            sleep_e.printStackTrace();
        }

        //crea i thread dei fumatori
        fumatori = new ArrayList<Smoker>();
        for(int i = 0; i < n_fumatori; i++) {
            System.out.println("- - - Creazione fumatore "+i+"... - - -");
            //fumatori.add(new Smoker(risorse, "smc#"+i,
            //                        max_smoke_time, lock_risorse,
            //                        sync_shop_smoker, lock_sync));
            fumatori.add(new Smoker(risorse, "smc#"+i,
                                    max_smoke_time, lock_risorse));
        }
    }
}
