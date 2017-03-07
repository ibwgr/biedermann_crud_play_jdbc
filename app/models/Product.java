package models;

import com.google.inject.Inject;
import play.db.Database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dieterbiedermann on 07.03.17.
 */
public class Product {

    public Integer id;
    public String ean;
    public String name;
    public String description;

    public Product() {
    }

    public Product(String ean, String name, String description) {
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    public Product(Integer id, String ean, String name, String description) {
        this.id = id;
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    public String toString() {
        return String.format("%s - %s", ean, name);
    }


    // Mocked Data
    private static List<Product> products;

    static {
        products = new ArrayList<Product>();
        products.add(new Product("11111111111111", "Paperclips 1", "Paperclips description 1"));
        products.add(new Product("22222222222222", "Paperclips 2", "Paperclips description 2"));
        products.add(new Product("33333333333333", "Paperclips 3", "Paperclips description 3"));
        products.add(new Product("44444444444444", "Paperclips 4", "Paperclips description 4"));
        products.add(new Product("55555555555555", "Paperclips 5", "Paperclips description 5"));
        products.add(new Product("66666666666666", "Paperclips 6", "Paperclips description 6"));
    }

    // DAO Methods
    public static List<Product> findAll() {
        return products;
    }

    public static Product findByEan(String ean) {
        for (Product candidate : products) {
            if (candidate.ean.equals(ean)) {
                return candidate;
            }
        }
        return null;
    }

    public static void addProduct(Product newProduct) {
        products.add(newProduct);
    }

    public void save() {
        products.remove(findByEan(this.ean));
        products.add(this);
    }
}
