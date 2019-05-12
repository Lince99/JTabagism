import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;



public class Main {

    //risorse pubbliche
    public static ArrayList<Component> risorse;

    public static void main(String[] args) {
        //utility
        Scanner scan = new Scanner(System.in);
        Monitor monitor;
        //smoker
        ArrayList<Thread> t_fmt;
        ArrayList<Smoker> fumatori;
        int n_fumatori = 0;
        int max_smoke_time = 0;
        int n_smoke = 0;
        ArrayList<Integer> quantity = new ArrayList<Integer>();
        //shop
        Thread t_tbc;
        Shop tabacchino;
        int check_q_tbc = 0;
        //shared
        /*
            code:
                -1: KILL
                0: ACK
                1: CHANGE
                2: END
            SHOP:
                -1: kill
                0: wait
                1: change, set to 0 (set to -1 if no resources)
                2: increment, set to 0
            SMOKER#n:
                -1: kill
                0: try extract (set to 1 if need resources)
                1: try extract (set to 1 if need resources)
                2: wait 0
                (set to 2 when finish)
        */
        SharedVar sync_shop_smoker = new SharedVar(1);
        Object lock_sync = new Object();
        Semaphore lock_risorse = new Semaphore(1);

        //Setup iniziale
        System.out.println("ARGS: ["+args.length+"]");
        for(int i = 0; i < args.length; i++)
            System.out.println("\t"+args[i]);
        if(args.length > 0)
            quantity.add(strToInt(args[0]));
        else {
            System.out.println("Quanto tabacco vuoi creare?");
            quantity.add(scan.nextInt());
        }
        if(args.length > 1)
            quantity.add(strToInt(args[1]));
        else {
            System.out.println("Quante cartine vuoi creare?");
            quantity.add(scan.nextInt());
        }
        if(args.length > 2)
            quantity.add(strToInt(args[2]));
        else {
            System.out.println("Quanti filtri vuoi creare?");
            quantity.add(scan.nextInt());
        }
        if(args.length > 3)
            quantity.add(strToInt(args[3]));
        else {
            System.out.println("Quanti accendini vuoi creare?");
            quantity.add(scan.nextInt());
        }
        monitor = new Monitor(quantity);
        if(args.length > 4)
                check_q_tbc = strToInt(args[4]);
        else {
            System.out.println("Ogni quanti secondi il tabacchino "+
                               "cambia vendite? (0 per sync)");
            check_q_tbc = scan.nextInt()*1000;
        }
        if(check_q_tbc == 0)
            System.out.println("\tSARA' ADOTTATA LA SYNC AUTOMATICA!");
        if(args.length > 5)
            n_fumatori = strToInt(args[5]);
        else {
            System.out.println("Quanti fumatori ci sono?");
            n_fumatori = scan.nextInt();
        }
        if(args.length > 6)
            n_smoke = strToInt(args[6]);
        else {
            System.out.println("Quante volte fumano?");
            n_smoke = scan.nextInt();
        }
        if(args.length > 7)
            max_smoke_time = strToInt(args[7])*1000;
        else {
            System.out.println("Quanti secondi fumano al massimo?");
            max_smoke_time = scan.nextInt()*1000;
        }
        if(args.length > 8)
            monitor.setWait_time(strToInt(args[8]));
        else {
            System.out.println("Quanti millisecondi aspettano per stampare?");
            monitor.setWait_time(scan.nextInt());
        }

        //Crea le risorse per i fumatori e il tabacchino
        risorse = new ArrayList<Component>();

        risorse.add(new Component("Tabacco  ", quantity.get(0)));
        risorse.add(new Component("Cartina  ", quantity.get(1)));
        risorse.add(new Component("Filtro   ", quantity.get(2)));
        risorse.add(new Component("Accendino", quantity.get(3)));

        //Crea il thread del tabacchino
        monitor.printString("- - - Creazione tabacchino... - - -");
        tabacchino = new Shop("tbc", risorse, lock_risorse,
                              monitor, sync_shop_smoker, lock_sync);
        tabacchino.setChange_time(check_q_tbc);
        //imposta la fine del tabacchino
        tabacchino.setMax_round(n_fumatori);
        //crea il thread del tabacchino
        t_tbc = new Thread(tabacchino, "tbc");
        //avvia il thread del tabacchino
        t_tbc.start();

        //crea i thread dei fumatori
        fumatori = new ArrayList<Smoker>();
        t_fmt = new ArrayList<Thread>();
        for(int i = 0; i < n_fumatori; i++) {
            monitor.printString("- - - Creazione fumatore "+i+"... - - -");
            fumatori.add(new Smoker("smc#"+i, risorse, lock_risorse, monitor,
                                    max_smoke_time, n_smoke,
                                    sync_shop_smoker, lock_sync));
            //crea i thread dei fumatori
            t_fmt.add(new Thread(fumatori.get(i), "smc#"+i));
        }
        //avvia i thread dei fumatori
        for(int i = 0; i < n_fumatori; i++)
            t_fmt.get(i).start();
    }

    private static int strToInt(String str) {
        int n = 0;

        try {
            n = Integer.parseInt(str);
        } catch(NumberFormatException num_e) {
            num_e.printStackTrace();
        }

        return n;
    }
}
