// Supplier.java
public class Supplier {
    private final int supplierId;
    private final Product product;

    public Supplier(int supplierId, Product product) {
        this.supplierId = supplierId;
        this.product = product;
    }

    public void deliver(Storage storage, int quantity) {
        storage.getInventory().merge(product, quantity, Integer::sum);
        System.out.println("Поставщик " + supplierId + " доставил " + quantity +
                " единиц товара " + product.getName());
    }

    public Product getProduct() {
        return product;
    }
}