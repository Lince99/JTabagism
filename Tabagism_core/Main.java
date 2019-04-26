package Tabagism/Tabagism_core;

public class Main {

    private

    public static void main(String[] args) {
        int n_fumatori;
        int max_timeout;
        Resource risorse[4];
        Semaphore sem = new Semaphore(2);

        risorse[0] = new Risorse("Tabacco");
        risorse[1] = new Risorse("Cartina");
        risorse[2] = new Risorse("Filtro");
        risorse[3] = new Risorse("Accendino");

        Shop tabacchino = new Shop(sem, "tbc");
    }
}
