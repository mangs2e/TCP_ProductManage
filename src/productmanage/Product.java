package productmanage;

public class Product {
    private int no;
    private String name;
    private int price;
    private int stock;

    public Product() {
    }

    public Product(int no, String name, int price, int stock) {
        this.no = no;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

//    @Override
//    public String toString() {
//        return "{'no': " + this.no
//                + ", 'name' : " + this.name
//                + ", 'price' : " + this.price
//                + ", 'stock' : " + this.stock + "}";
//    }
}
