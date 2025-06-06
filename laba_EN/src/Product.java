public class Product {
    private final String name;
    private final int storeId;
    private final int supplierId;
    private final double price;
    private final int providerId;

    public Product(String name, int storeId, int supplierId, double price, int providerId) {
        this.name = name;
        this.storeId = storeId;
        this.supplierId = supplierId;
        this.price = price;
        this.providerId = providerId;
    }

    public String getName() {return name;}
    public int getStoreId() {return storeId;}
    public int getSupplierId() {return supplierId;}
    public double getPrice() {return price;}
    public int getProviderId() {return providerId;}
}