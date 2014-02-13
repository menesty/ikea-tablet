package org.menesty.ikea.tablet;

import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductState {
    private List<AvailableProductItem> baseState = new ArrayList<AvailableProductItem>();

    private Map<String, Double> state = new HashMap<String, Double>();

    public void setBaseState(List<AvailableProductItem> baseState) {
        this.baseState = baseState;
        reset();
    }

   /* public List<AvailableProductItem> getCurrentState() {
        return currentState;
    }

    public AvailableProductItem find(String productId) {
        for (AvailableProductItem product : currentState)
            if (product.productId.equals(productId))
                return product;

        return null;
    }

    public void takeProduct(AvailableProductItem product) {
        if (!product.visible && !product.allowed)
            throw new RuntimeException("Can't update state because product is not visible or allowed");

        for (AvailableProductItem cProduct : currentState)
            if (cProduct.productId.equals(product.productId)) {
                cProduct.count = cProduct.count - product.count;
                return;
            }
    }

    public void returnProduct(AvailableProductItem product) {
        for (AvailableProductItem cProduct : currentState)
            if (cProduct.productId.equals(product.productId)) {
                cProduct.count = cProduct.count + product.count;
                return;
            }

        currentState.add(product);
    }
*/
    public void reset() {
        state = new HashMap<String, Double>();
    }
}
