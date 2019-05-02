package Tabagism.Tabagism_core;

import java.util.ArrayList;



public class Main {

    private int n_fumatori = 4;
    private int max_timeout = 10;

    public static void main(String[] args) {
        int tabacco_q = 10000;
        int cartina_q = 1000;
        int filtro_q = 1000;
        int accendino_q = 100;
        static ArrayList risorse;
        int check_q_tbc = 10000; //ogni 10 secondi controlla le risorse

        //Crea le risorse per i fumatori e il tabacchino
        risorse = new ArrayList();
        risorse.add(new Component("Tabacco", tabacco_q));
        risorse.add(new Component("Cartina", cartina_q));
        risorse.add(new Component("Filtro", filtro_q));
        risorse.add(new Component("Accendino", accendino_q));

        //Crea il thread del tabacchino
        Shop tabacchino = new Shop(risorse, "tbc");
        tabacchino.setChange_time(check_q_tbc);

        //Crea i thread dei fumatori
        ArrayList fumatori = new ArrayList();
        for(int i = 0; i < n_fumatori; i++) {
            fumatori.add(new Smoker(risorse));
        }
    }
}
