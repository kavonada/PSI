package model.use_case_3;

public class KosikPolozka {
    private final Material material;
    private final int quantity;
    private final double price;

    public KosikPolozka(Material material, int quantity, double price) {
        this.material = material;
        this.quantity = quantity;
        this.price = price;
    }

    public Material getMaterial() { return material; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
