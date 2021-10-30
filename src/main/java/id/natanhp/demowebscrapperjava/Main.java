package id.natanhp.demowebscrapperjava;

import id.natanhp.demowebscrapperjava.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Product> products = new ArrayList<>();

        do {
            products.addAll(scrapProduct());
        } while (products.size() < 100);

        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("Phones.csv"));

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.Builder.create().setHeader("Name", "Description", "Image Link", "Price", "Rating", "Store Name").setDelimiter(";").build());

            products.forEach(product -> {
                try {
                    csvPrinter.printRecord(product.getName(), product.getDescription(), product.getImageLink(), product.getPrice(), product.getRating(), product.getStoreName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            csvPrinter.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<Product> scrapProduct() {
        ArrayList<Product> tempProducts = new ArrayList<>();
        try {
            Document document = Jsoup.connect("https://www.tokopedia.com/search?navsource=home&page=2&q=handphone&source=universe&srp_component_id=02.02.02.01&st=product").get();

            Elements listOfProducts = document.getElementsByClass("pcv3__info-content css-1qnnuob");

            for (Element listOfProduct : listOfProducts) {
                String productLink = listOfProduct.attr("href");

                if (!productLink.startsWith("https://ta.")) {
                    Product product = new Product();

                    for (Element rating : listOfProduct.getElementsByClass("css-etd83i")) {
                        product.setRating(rating.text());
                    }

                    Elements shopNameFields = listOfProduct.getElementsByClass("css-qjiozs flip");

                    if (!shopNameFields.isEmpty()) {
                        if (shopNameFields.get(1) != null) {
                            product.setStoreName(shopNameFields.get(1).text());
                        }
                    }

                    Document productDetail = Jsoup.connect(productLink).get();


                    Elements productNames = productDetail.getElementsByAttributeValue("data-testid", "lblPDPDetailProductName");
                    for (Element productName : productNames) {
                        product.setName(productName.text());
                    }

                    Elements productDescriptions = productDetail.getElementsByAttributeValue("data-testid", "lblPDPDescriptionProduk");
                    for (Element productDescription : productDescriptions) {
                        product.setDescription(productDescription.text());
                    }

                    Elements imageProducts = productDetail.getElementsByAttributeValue("data-testid", "PDPImageMain");
                    for (Element imageProduct : imageProducts) {
                        Elements imgs = imageProduct.getElementsByTag("img");

                        for (Element img : imgs) {
                            product.setImageLink(img.attr("src"));
                        }
                    }

                    Elements productPrices = productDetail.getElementsByAttributeValue("data-testid", "lblPDPDetailProductPrice");
                    for (Element productPrice : productPrices) {
                        product.setPrice(productPrice.text());
                    }

                    tempProducts.add(product);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return tempProducts;
    }
}
