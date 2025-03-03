package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart(value = "product") @Valid ProductDTO productDTO,
            @RequestPart(value = "image",required = false)MultipartFile image) throws IOException {
        return  ResponseEntity.ok(productService.createProduct(productDTO,image));
    }

    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart(value = "product") @Valid ProductDTO productDTO,
            @RequestPart(value = "image",required = false) MultipartFile image) throws IOException{
        return ResponseEntity.ok(productService.updateProduct(id,productDTO,image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id){
        return  ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getAllProducts(){
        return  ResponseEntity.ok(productService.getAllProducts());
    }
}
