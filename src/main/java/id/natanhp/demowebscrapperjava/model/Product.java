package id.natanhp.demowebscrapperjava.model;

import lombok.Data;

@Data
public class Product {
    private String name;
    private String description;
    private String imageLink;
    private String price;
    private String rating;
    private String storeName;
}
