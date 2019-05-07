import java.util.ArrayList;

public class Monitor {

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
    }
}
