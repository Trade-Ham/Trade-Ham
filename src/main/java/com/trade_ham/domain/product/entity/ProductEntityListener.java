package com.trade_ham.domain.product.entity;

import com.trade_ham.domain.product.repository.ProductSearchRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEntityListener {
    private final ProductSearchRepository productSearchRepository;

    @PostPersist
    @PostUpdate
    public void onAfterSave(ProductEntity product) {
        ProductDocument document = ProductDocument.from(product);
        productSearchRepository.save(document);
    }

    @PostRemove
    public void onAfterDelete(ProductEntity product) {
        productSearchRepository.deleteById(product.getProductId());
    }
}
