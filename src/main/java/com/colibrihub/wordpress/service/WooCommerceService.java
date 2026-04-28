package com.colibrihub.wordpress.service;

import com.colibrihub.wordpress.dto.CreateProductRequest;
import com.colibrihub.wordpress.dto.WooProductDto;

public interface WooCommerceService {

    WooProductDto createProduct(CreateProductRequest request);
}
