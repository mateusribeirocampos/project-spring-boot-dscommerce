package com.dscommerce.dto;

import com.dscommerce.entities.Product;
import java.math.BigDecimal;

public class ProductMinDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private String imgUrl;

    public ProductMinDTO() {}

    public ProductMinDTO(Long id, String name, BigDecimal price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProductMinDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
