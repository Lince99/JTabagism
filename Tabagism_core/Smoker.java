package Tabagism/Tabagism_core;

import java.util.

public class Smoker extends Thread {

    Shop tabacchino;

    public Smoker(Shop s) {
        tabacchino = s;
    }

    @Override
    public void run() {
        try {
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
