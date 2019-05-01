package Tabagism.Tabagism_core;

import java.util.concurrent.Semaphore;

public class Shop extends Thread {

    Semaphore sem;
    String t_name;
    //convert to arrayList
    Component shared_resource[];
    Component local_resource[];

    public Shop(Component c) {
        /*this.local_resource = Component.initComponentArr(4,
                                                    {"Tabacco", "Cartina",
                                                     "Filtro", "Accendino"},
                                                    {0, 0, 0, 0});*/
    }

    @Override
    public void run() {
        //shop do stuff

    }
}
