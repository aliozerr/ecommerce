package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CommentDTO;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.model.Comment;
import com.example.ecommerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target ="productId" ,source = "product.id")
    ProductDTO toDto(Product product);

    @Mapping(target = "product.id",source = "productId")
    Product toEntity(ProductDTO productDTO);


}
