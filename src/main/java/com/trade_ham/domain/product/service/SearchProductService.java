package com.trade_ham.domain.product.service;

import com.trade_ham.domain.product.dto.ProductResponseDTO;
import com.trade_ham.domain.product.entity.ProductDocument;
import com.trade_ham.domain.product.entity.ProductStatus;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.domain.product.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.SearchHit;


@Service
@RequiredArgsConstructor
public class SearchProductService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

//    @Transactional(readOnly = true)
//    public List<ProductResponseDTO> searchProducts(String keyword) {
//        List<ProductEntity> products = productRepository.searchProducts(keyword);
//
//        return products.stream()
//                .map(ProductResponseDTO::new)
//                .collect(Collectors.toList());
//    }

//    @Transactional(readOnly = true)
//    public List<ProductResponseDTO> searchProducts(String keyword) {
//        Query query = NativeQuery.builder()
//                .withQuery(q -> q
//                        .bool(b -> b
//                                .should(s -> s
//                                        .match(m -> m
//                                                .field("name")
//                                                .query(keyword)
//                                        )
//                                )
//                                .should(s -> s
//                                        .match(m -> m
//                                                .field("description")
//                                                .query(keyword)
//                                        )
//                                )
//                                .minimumShouldMatch("1")
//                                .filter(f -> f
//                                        .term(t -> t
//                                                .field("status")
//                                                .value("SELL")
//                                        )
//                                )
//                        )
//                )
//                .build();
//
//        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(
//                query,
//                ProductDocument.class
//        );
//
//        return searchHits.getSearchHits().stream()
//                .map(SearchHit::getContent)
//                .map(doc -> productRepository.findById(doc.getId())
//                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + doc.getId())))
//                .map(ProductResponseDTO::new)
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(String keyword) {
        List<ProductDocument> products = productSearchRepository.
                findByNameContainingOrDescriptionContainingAndStatus(
                        keyword, keyword, ProductStatus.SELL
                );

        return products.stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

}