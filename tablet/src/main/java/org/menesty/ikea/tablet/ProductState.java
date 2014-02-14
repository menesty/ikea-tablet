package org.menesty.ikea.tablet;

import org.menesty.ikea.tablet.domain.AvailableProductItem;
import org.menesty.ikea.tablet.domain.ProductItem;

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

    public List<ProductItem> getCurrentState() {
        List<ProductItem> productItems = new ArrayList<ProductItem>();

        for (AvailableProductItem productItem : baseState) {
            if (productItem.allowed && productItem.visible) {
                Double count = state.get(productItem.productId + "_" + productItem.price);
                count = count == null ? 0 : count;

                if (count == 0 || count < productItem.count)
                    productItems.add(new ProductItem(productItem.productId, productItem.count - count, productItem.price, productItem.weight));
            }
        }
        return productItems;
    }

    public ProductItem find(String productId) {
        for (AvailableProductItem productItem : baseState)
            if (productItem.productId.equals(productId) && productItem.allowed && productItem.visible) {

                Double count = state.get(productItem.productId + "_" + productItem.price);
                count = count == null ? 0 : count;

                if (count == 0 || count < productItem.count)
                    return new ProductItem(productItem.productId, productItem.count - count, productItem.price, productItem.weight);

                return null;
            }

        return null;
    }



    /*

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
