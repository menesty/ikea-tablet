package org.menesty.ikea.tablet.data;

import android.util.JsonReader;
import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public List<AvailableProductItem> readJsonStream(InputStream in) throws IOException {

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
        int count = 0;
        int weight = 0;
        double price = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("productName")) {
                productName = reader.nextString();
            } else if (name.equals("count")) {
                count = reader.nextInt();
            } else if (name.equals("weight")) {
                weight = reader.nextInt();
            } else if (name.equals("price")) {
                price = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new AvailableProductItem(productName, count, price, weight);
    }

}
