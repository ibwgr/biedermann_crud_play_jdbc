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

        Connection conn = db.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into product (ean, name, description) values(?, ?, ?)");
            stmt.setString(1, newProduct.ean);
            stmt.setString(2, newProduct.name);
            stmt.setString(3, newProduct.description);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError();
        }

        Product.addProduct(newProduct);
        return ok(Json.toJson(newProduct));
    }
    /*
    for testing:
curl -v --header "Content-type: application/json" --request PUT --data '{"ean": "2000222", "name":"Folder Yellow", "description":"A yellow folder"}' http://localhost:9000/product/new
     */

    public Result details(String ean) {
        ArrayList<Product> products = new ArrayList<>();
        Connection conn = db.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select * from product where ean = ?");
            stmt.setString(1, ean);
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
    public Result save() {
        JsonNode json = request().body().asJson();
        Product newProduct = Json.fromJson(json, Product.class);

        Connection conn = db.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("update product set name = ?, description = ? where ean = ?");
            stmt.setString(1, newProduct.name);
            stmt.setString(2, newProduct.description);
            stmt.setString(3, newProduct.ean);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError();
        }

        return ok(Json.toJson(newProduct));
    }
    /*
    for testing:
curl -v --header "Content-type: application/json" --request POST --data '{"ean": "2000222", "name":"Folder Yellow new NAME", "description":"A yellow folder"}' http://localhost:9000/product/
     */

}
