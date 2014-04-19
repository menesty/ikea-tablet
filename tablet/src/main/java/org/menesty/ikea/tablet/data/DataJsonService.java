package org.menesty.ikea.tablet.data;

import android.util.JsonReader;
import android.util.JsonWriter;
import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class DataJsonService {

    public List<AvailableProductItem> parseProducts(InputStream in) throws IOException {

        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readProductsArray(reader);
        } finally {
            reader.close();
        }
    }

    private List<AvailableProductItem> readProductsArray(JsonReader reader) throws IOException {
        List<AvailableProductItem> messages = new ArrayList<AvailableProductItem>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    private AvailableProductItem readMessage(JsonReader reader) throws IOException {
        String productName = null;
        String productId = null;
        double count = 0;
        double weight = 0;
        double price = 0;
        boolean zestav = false;
        String shortName = null;
        boolean allowed = true;
        int orderId = 0;
        boolean visible = true;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("productNumber"))
                productName = reader.nextString();
            else if (name.equals("productId"))
                productId = reader.nextString();
            else if (name.equals("count"))
                count = reader.nextDouble();
            else if (name.equals("weight"))
                weight = reader.nextDouble();
            else if (name.equals("price"))
                price = reader.nextDouble();
            else if (name.equals("zestav"))
                zestav = reader.nextString().equals("1");
            else if (name.equals("shortName"))
                shortName = reader.nextString();
            else if (name.equals("allowed"))
                allowed = reader.nextString().equals("1");
            else if (name.equals("orderId"))
                orderId = reader.nextInt();
            else if (name.equals("visible"))
                visible = reader.nextString().equals("1");
            else
                reader.skipValue();

        }
        reader.endObject();
        return new AvailableProductItem(productId, productName, shortName, count, price, weight, zestav, allowed, visible, orderId);
    }


    public String serializeParagons(List<ProductItem[]> data) {
        StringWriter sw = new StringWriter();
        try {
            JsonWriter writer = new JsonWriter(sw);

            writer.beginObject();
            writer.name("driverId").value(1);
            writer.name("paragons");

            writer.beginArray();

            for (int i = 0; i < data.size(); i++) {
                ProductItem[] items = data.get(i);

                if(items.length == 0)
                    continue;

                writer.beginObject();
                writer.name("paragonId").value(i);
                writer.name("userId").value(i);
                writer.name("items");

                writer.beginArray();

                int orderId = 0;
                if (items.length > 0) {
                    orderId = items[0].orderId;

                    for (ProductItem item : items) {
                        writer.beginObject();
                        writer.name("productNumber").value(item.productName);
                        writer.name("price").value(item.price);
                        writer.name("count").value(item.count);
                        writer.name("weight").value(item.weight);
                        writer.endObject();
                    }
                }

                writer.endArray();

                writer.name("orderId").value(orderId);

                writer.endObject();

            }
            writer.endArray();

            writer.endObject();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

}
