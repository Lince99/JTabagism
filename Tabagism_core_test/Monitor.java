import java.util.ArrayList;



public class Monitor {
    private ArrayList<Integer> max_size;
    private int sector_max;
    private int wait_time;

    public Monitor(ArrayList<Integer> max_size) {
        this.max_size = max_size;
        this.sector_max = 40;
    }

    public Monitor(ArrayList<Integer> max_size, int max) {
        this.max_size = max_size;
        this.sector_max = max;
    }

    public ArrayList<Integer> getMax_size() {
        return this.max_size;
    }
    public void setMax_size(ArrayList<Integer> arr) {
        this.max_size = arr;
    }
    public void setMax_size_elem(int pos, int val) {
        this.max_size.set(pos, val);
    }

    public int getWait_time() {
        return this.wait_time;
    }
    public void setWait_time(int time) {
        this.wait_time = time;
    }

    public void printString(String str) {
        System.out.println(str);
    }

    //stampa formattata con delle barre orizzontali per indicare la quantita'
    public void printInfo(String name, ArrayList<Component> arr) {
        int size = arr.size();
        int j;
        int i;
        int curr_q = 0;
        int sector_q = 0;
        int max = 0;
        char draw_ch = '#';
        char void_ch = '.';

        /*
        name:
            arr[0] = |######....|
            arr[1] = |####......|
            arr[2] = |########..|
            arr[0] = |#.........|
        */
        System.out.println(name+":");
        if(size == 0) {
            System.out.println("\tnull");
            return;
        }
        System.out.flush();
        //rende la stampa leggibile
        if(this.wait_time > 0) {
            try {
                Thread.sleep((int)(this.wait_time/2));
            } catch(InterruptedException sleep_e) {
                sleep_e.printStackTrace();
            }
        }
        for(i = 0; i < size; i++) {
            System.out.print("\t"+arr.get(i).getType()+"=\t|");
            max = max_size.get(i);
            curr_q = arr.get(i).getQuantity();
            if(curr_q == 0) {
                for(j = 0; j < (int)(this.sector_max/2)-4; j++) {
                    System.out.print(void_ch);
                }
                System.out.print("void");
                for(j = (int)(this.sector_max/2)-4; j < sector_max-4; j++) {
                    System.out.print(void_ch);
                }
            }
            else {
                //curr_q : max = sector_q : sector_max
                sector_q = (int)( (curr_q*sector_max) / max );
                if(sector_q == 0)
                    sector_q = 1;
                for(j = 0; j < sector_q; j++) {
                    System.out.print(draw_ch);
                }
                for(j = 0; j < this.sector_max-sector_q; j++) {
                    System.out.print(void_ch);
                }
            }
            System.out.print("|\t"+curr_q+"\n");
            System.out.flush();
        }
        //rende la stampa leggibile
        if(this.wait_time > 0) {
            try {
                Thread.sleep((int)(this.wait_time/2));
            } catch(InterruptedException sleep_e) {
                sleep_e.printStackTrace();
            }
        }
    }

    //stampa la lista dei componenti e i corrispettivi valori
    public void printListInfo(ArrayList<Component> arr) {
        int size = arr.size();

        System.out.print("["+size+"] = ");
        if(size == 0) {
            System.out.println("null");
            return;
        }
        for(int i = 0; i < size; i++) {
            System.out.print(arr.get(i).getType()+"="+arr.get(i).getQuantity());
            if(i == size-1)
                System.out.println(".");
            else
                System.out.print(", ");
        }
        System.out.flush();
    }
}
