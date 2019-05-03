package Tabagism.Tabagism_core;

import java.util.ArrayList;



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

        //Setup iniziale
        n_fumatori = 4;
        max_smoke_time = 10000;
        //Crea le risorse per i fumatori e il tabacchino
        risorse = new ArrayList<Component>();
        risorse.add(new Component("Tabacco", tabacco_q));
        risorse.add(new Component("Cartina", cartina_q));
        risorse.add(new Component("Filtro", filtro_q));
        risorse.add(new Component("Accendino", accendino_q));

        //Crea il thread del tabacchino
        tabacchino = new Shop(risorse, "tbc");
        tabacchino.setChange_time(check_q_tbc);

        //Crea i thread dei fumatori
        fumatori = new ArrayList<Smoker>();
        for(int i = 0; i < n_fumatori; i++) {
            fumatori.add(new Smoker(risorse, max_smoke_time));
        }
    }
}
