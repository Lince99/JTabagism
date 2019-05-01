package Tabagism.Tabagism_GUI;

import org.gnome.gtk.*;

public class GUI_Main {

    public GUI_Main(String[] args) {
        Gtk.init(args);
        new Label();
        Builder b = new Builder();
        try {
            b.addFromFile("GUI_MainWindow.glade");
        } catch(Exception fe) {
            fe.printStackTrace();
        }
        Window w = (Window) b.getObject("GtkApplicationWindow");
        w.showAll();
        Gtk.main();
    }
}
