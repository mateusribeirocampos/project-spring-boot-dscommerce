package com.dscommerce.services;

import com.dscommerce.dto.ProductDTO;
import com.dscommerce.entities.Product;
import com.dscommerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String name, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByName(name, pageable);
        return productPage.map(ProductDTO::new);
    }

}
