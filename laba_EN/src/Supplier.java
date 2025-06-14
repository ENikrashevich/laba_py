public class Supplier {
    private final int supplierId;
    private final Product product;
    private int quantity;

    public Supplier(int supplierId, Product product, int quantity) {
        this.supplierId = supplierId;
        this.product = product;
        this.quantity = quantity;
    }

    public void deliver(Storage storage, int quantity) {
        if (this.quantity >= quantity && this.quantity > 0){
SimulationTime.advanceTime(45);

storage.getInventory().merge(product, quantity, Integer::sum);
            System.out.println("Поставщик " + supplierId + " доставил " + quantity +
                    " единиц товара " + product.getName());
        }
        else {
            storage.getInventory().merge(product, this.quantity, Integer::sum);
            System.out.println("Поставщик " + supplierId + " доставил " + this.quantity +
                    " единиц товара " + product.getName());
            this.quantity -= quantity;
        }
    }

    public Product getProduct() {return product;}
}