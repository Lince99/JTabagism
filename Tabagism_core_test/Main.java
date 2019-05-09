import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Scanner;



public class Main {

    //risorse pubbliche
    public static ArrayList<Component> risorse;

    public static void main(String[] args) {
        //utility
        Scanner scan = new Scanner(System.in);
        //smoker
        ArrayList<Thread> t_fmt;
        ArrayList<Smoker> fumatori;
        int n_fumatori = 0;
        int max_smoke_time = 0;
        int tabacco_q = 0;
        int cartina_q = 0;
        int filtro_q = 0;
        int accendino_q = 0;
        //shop
        Thread t_tbc;
        Shop tabacchino;
        int check_q_tbc = 0;
        //shared
        SharedVar sync_shop_smoker = new SharedVar();
        Object lock_sync = new Object();
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
        System.out.println("Ogni quanti secondi il tabacchino cambi vendite?");
        check_q_tbc = scan.nextInt()*1000;
        if(check_q_tbc == 0)
            System.out.println("SARA' ADOTTATA LA SYNC AUTOMATICA!");
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

        //Crea l'oggetto tabacchino
        System.out.println("- - - Creazione tabacchino... - - -");
        if(check_q_tbc != 0)
            tabacchino = new Shop(risorse, "tbc", lock_risorse, check_q_tbc);
        else {
            tabacchino = new Shop(risorse, "tbc", lock_risorse,
                                  sync_shop_smoker, lock_sync);
        }
        //crea gli oggetti fumatori
        fumatori = new ArrayList<Smoker>();
        for(int i = 0; i < n_fumatori; i++) {
            System.out.println("- - - Creazione fumatore "+i+"... - - -");
            if(max_smoke_time != 0)
                fumatori.add(new Smoker(risorse, "smc#"+i,
                                        max_smoke_time, lock_risorse));
            else
                fumatori.add(new Smoker(risorse, "smc#"+i,
                                        lock_risorse, max_smoke_time,
                                        sync_shop_smoker, lock_sync));
        }

        //crea il thread del tabacchino
        t_tbc = new Thread(tabacchino, "tbc");
        //crea i thread dei fumatori
        t_fmt = new ArrayList<Thread>();
        for(int i = 0; i < n_fumatori; i++) {
            t_fmt.add(new Thread(fumatori.get(i), "smc#"+i));
        }

        //avvia il thread del tabacchino
        t_tbc.start();
        //lascia un po di tempo al thread del tabacchino per occupare le risorse
        try {
            Thread.sleep(1000);
        } catch(InterruptedException sleep_e) {
            sleep_e.printStackTrace();
        }
        //avvia i thread dei fumatori
        for(int i = 0; i < n_fumatori; i++)
            t_fmt.get(i).start();
    }
}
