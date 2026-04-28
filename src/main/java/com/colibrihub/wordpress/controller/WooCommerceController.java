package com.colibrihub.wordpress.controller;

import com.colibrihub.wordpress.dto.ApiResponse;
import com.colibrihub.wordpress.dto.CreateProductRequest;
import com.colibrihub.wordpress.dto.WooProductDto;
import com.colibrihub.wordpress.service.WooCommerceService;
import com.colibrihub.wordpress.service.WordpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class WooCommerceController {

    private final WooCommerceService wooCommerceService;

    public WooCommerceController(WooCommerceService wooCommerceService){
        this.wooCommerceService = wooCommerceService;
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<WooProductDto>> createProduct(@RequestBody CreateProductRequest request){
        WooProductDto product = wooCommerceService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado en WooCommerce", product));
    }

}
