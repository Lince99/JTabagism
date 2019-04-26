package Tabagism/Tabagism_core;

public class Resource {

    private String type;
    private int quantity;

    public Resource(String t) {
        this.type = t;
        this.quantity = 0;
    }

    public Resource(String t, int q) {
        this.type = t;
        this.quantity = q;
    }

    public void setType(String t) {
        this.type = t;
    }
    public void setQuantity(int q) {
        this.quantity = q;
    }

    public String getType() {
        return this.type;
    }
    public int getQuantity() {
        return this.quantity;
    }
}
