package org.menesty.ikea.tablet;

import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.util.*;

public class ProductState {
    private List<AvailableProductItem> baseState = new ArrayList<AvailableProductItem>();

    private Map<String, Double> state = new HashMap<String, Double>();

    public void setBaseState(AvailableProductItem... baseState) {
        setBaseState(Arrays.asList(baseState));
    }

    public void setBaseState(List<AvailableProductItem> baseState) {
        this.baseState = baseState;
        reset();
    }

    public ProductItem[] getCurrentState() {
        List<ProductItem> productItems = new ArrayList<ProductItem>();

        for (AvailableProductItem productItem : baseState) {
            if (productItem.allowed && productItem.visible) {
                Double count = state.get(getKey(productItem));
                count = count == null ? 0 : count;

                if (count == 0 || count < productItem.count)
                    productItems.add(new ProductItem(productItem.productId, productItem.productName, productItem.shortName, productItem.count - count, productItem.price, productItem.weight, productItem.orderId));
            }
        }
        return productItems.toArray(new ProductItem[productItems.size()]);
    }

    public ProductItem find(String productId) {
        for (AvailableProductItem productItem : baseState)
            if (productItem.productId.equals(productId) && productItem.allowed && productItem.visible) {
                Double count = state.get(getKey(productItem));
                count = count == null ? 0 : count;

                if (count == 0 || count < productItem.count)
                    return new ProductItem(productItem.productId, productItem.productName,  productItem.shortName, productItem.count - count, productItem.price, productItem.weight, productItem.orderId);

            }

        return null;
    }

    public void reset() {
        state = new HashMap<String, Double>();
    }

    public void takeProduct(ProductItem product) {
        String key = getKey(product);
        Double count = state.get(key);

        if (count == null)
            count = 0d;

        state.put(key, count + product.count);

    }

    private String getKey(ProductItem product){
        return product.productName + "_" + product.price;
    }

    private String getKey(AvailableProductItem productItem){
        return productItem.productName + "_" + productItem.price;
    }


    public AvailableProductItem[] getBaseState() {
        return baseState.toArray(new AvailableProductItem[baseState.size()]);
    }

    public String[] getState() {
        List<String> result = new ArrayList<String>();

        for (Map.Entry<String, Double> entry : state.entrySet())
            result.add(entry.getKey() + "|" + entry.getValue());

        return result.toArray(new String[result.size()]);
    }

    public void setState(String... states) {
        reset();
        for (String state : states) {
            String[] parts = state.split("|");
            Double count;
            try {
                count = Double.valueOf(parts[1]);
                this.state.put(parts[0], count);
            } catch (NumberFormatException e) {

            }
        }
    }

    public void returnBack(ProductItem product, double count) {
        String key = product.productName + "_" + product.price;
        Double c = state.get(key);

        if (c == null)
            throw new RuntimeException("Product not found");

        if (c - count == 0)
            state.remove(key);
        else
            state.put(key, c - count);

    }
}
