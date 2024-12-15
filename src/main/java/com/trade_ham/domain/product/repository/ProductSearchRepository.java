package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.product.entity.ProductDocument;
import com.trade_ham.domain.product.entity.ProductStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
//    List<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description);

//    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match\": {\"name\": \"?0\"}}, {\"match\": {\"description\": \"?0\"}}]}}, {\"term\": {\"status\": \"SELL\"}}]}}")
//    List<ProductDocument> findByNameContainingOrDescriptionContainingAndStatus(String keyword, ProductStatus status);

//    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match\": {\"name\": \"?0\"}}, {\"match\": {\"description\": \"?0\"}}]}}, {\"term\": {\"status.keyword\": \"?1\"}}]}}")
//    List<ProductDocument> findByNameContainingOrDescriptionContainingAndStatus(String keyword, ProductStatus status);

    List<ProductDocument> findByNameContainingOrDescriptionContainingAndStatus(
            String name,
            String description,
            ProductStatus status
    );
}
