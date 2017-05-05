package buyerseller.cs646.sdsu.edu.sellit;

import java.io.Serializable;

/**
 * Created by vk on 4/30/17.
 */
public class ItemModel {
    private String itemId;
    private String itemName;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private String sellingCost;
    private String itemTitle;
    private String itemDescription;
    private String imageUrl;

    public ItemModel()
    {

    }


    public ItemModel(String itemId, String itemName, String buyerId, String buyerName, String sellerId, String sellerName, String sellingCost, String itemTitle, String itemDescription, String imageUrl) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellingCost = sellingCost;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.imageUrl = imageUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellingCost() {
        return sellingCost;
    }

    public void setSellingCost(String sellingCost) {
        this.sellingCost = sellingCost;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }




}