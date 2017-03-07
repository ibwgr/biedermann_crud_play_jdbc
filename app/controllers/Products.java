package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Product;
import play.db.Database;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dieterbiedermann on 07.03.17.
 */
public class Products extends Controller {

    private Database db;

    @Inject
    public Products(Database db) {
        this.db = db;
    }

    public Result list() {
        ArrayList<Product> products = new ArrayList<>();
        Connection conn = db.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select * from product");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("ean"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError();
        }

        return ok(Json.toJson(products));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result newProduct() {
        JsonNode json = request().body().asJson();
        Product newProduct = Json.fromJson(json, Product.class);
        Product.addProduct(newProduct);
        return ok(Json.toJson(newProduct));
    }
    /*
    for testing:
curl -v --header "Content-type: application/json" --request PUT --data '{"ean": "2000222", "name":"Folder Yellow", "description":"A yellow folder"}' http://localhost:9000/products/new
     */

    public Result details(String ean) {

        Product product = Product.findByEan(ean);

        JsonNode json = Json.toJson(product);

        return ok(json);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result save() {
        JsonNode json = request().body().asJson();
        Product newProduct = Json.fromJson(json, Product.class);
        newProduct.save();
        return ok(Json.toJson(newProduct));
    }
    /*
    for testing:
curl -v --header "Content-type: application/json" --request POST --data '{"ean": "2000222", "name":"Folder Yellow", "description":"A yellow folder"}' http://localhost:9000/products/
     */

}
