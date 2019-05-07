package Tabagism.Tabagism_core;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;



public class Main {

    private static int n_fumatori;
    private static int max_smoke_time;
    public static ArrayList<Component> risorse;
    private static Monitor monitor = new Monitor();

    public static void main(String[] args) {
        int tabacco_q = 100;
        int cartina_q = 100;
        int filtro_q = 100;
        int accendino_q = 100;
        int check_q_tbc = 3000; //ogni 3 secondi controlla le risorse
        Shop tabacchino;
        ArrayList<Smoker> fumatori;
        Semaphore lock_risorse = new Semaphore(1);

        //Setup iniziale
        n_fumatori = 4;
        max_smoke_time = 1000; //(int)(check_q_tbc / 2);
        //Crea le risorse per i fumatori e il tabacchino
        risorse = new ArrayList<Component>();
        risorse.add(new Component("Tabacco", tabacco_q));
        risorse.add(new Component("Cartina", cartina_q));
        risorse.add(new Component("Filtro", filtro_q));
        risorse.add(new Component("Accendino", accendino_q));

        System.out.print("\nMAIN\t");
        monitor.printListInfo(risorse);

        //Crea il thread del tabacchino
        System.out.println("- - - Creazione tabacchino... - - -");
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
            fumatori.add(new Smoker(risorse, "smc#"+i,
                                    max_smoke_time, lock_risorse));
        }
    }
}
