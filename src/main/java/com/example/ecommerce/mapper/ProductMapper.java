package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CommentDTO;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.model.Comment;
import com.example.ecommerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target ="image" ,source = "image")
    ProductDTO toDto(Product product);

    @Mapping(target = "image",source = "image")
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "userId",source = "user.id")
    CommentDTO toDTO(Comment comment);
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "product", ignore = true)
    Comment toEntity(CommentDTO commentDTO);
}
