package loozer.billgen;

/**
 * Created by Loozer on 4/24/2016.
 */
public class Product {
    public enum ProductName{Ghee,Butter}
    ProductName type = null;
    double qty = 0.0;
    double rate = 0.0;
    double price = 0.0;

    public Product(){
    }

    public Product(ProductName type,double rate){
        this.type = type;
        this.rate = rate;
    }

    public ProductName getType() {
        return type;
    }

    public void setType(ProductName type) {
        this.type = type;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
