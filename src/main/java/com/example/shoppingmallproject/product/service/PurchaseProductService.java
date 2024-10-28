// PurchaseProductService.java
package com.example.shoppingmallproject.product.service;

import com.example.shoppingmallproject.common.exception.AccessDeniedException;
import com.example.shoppingmallproject.common.exception.InvalidProductStateException;
import com.example.shoppingmallproject.common.exception.ResourceNotFoundException;
import com.example.shoppingmallproject.common.exception.ErrorCode;
import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.repository.LockerRepository;
import com.example.shoppingmallproject.product.domain.Product;
import com.example.shoppingmallproject.product.domain.ProductStatus;
import com.example.shoppingmallproject.product.domain.Trade;
import com.example.shoppingmallproject.product.repository.ProductRepository;
import com.example.shoppingmallproject.product.repository.TradeRepository;
import com.example.shoppingmallproject.user.domain.User;
import com.example.shoppingmallproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseProductService {
    private final ProductRepository productRepository;
    private final LockerRepository lockerRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;

    /*
    사용자가 구매 요청 버튼 클릭
    -> 해당 물품에 락 걸기
    -> 물품 상태 변경
     */
    @Transactional
    public Product purchaseProduct(Long productId) {
        // 동시성을 고려해 비관적 락을 사용
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getStatus().equals(ProductStatus.SELL)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        // Product 상태를 'CHECK'로 변경
        product.setStatus(ProductStatus.CHECK);
        productRepository.save(product);

        return product;
    }

    /*
     구매 완료 버튼 클릭
     물품 상태 변경
     물품에 사물함을 할당하고 사물함 상태 변경
     (추후 판매자에게 알림을 보내주는 서비스 구현)
     거래 내역 생성
     */
    @Transactional
    public Trade completePurchase(Long productId, Long buyerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getStatus().equals(ProductStatus.CHECK)) {
            throw new InvalidProductStateException(ErrorCode.INVALID_PRODUCT_STATE);
        }

        // 상태를 WAIT으로 변경
        product.setStatus(ProductStatus.WAIT);

        // 사용 가능한 사물함 할당
        Locker availableLocker = lockerRepository.findFirstByLockerStatusTrue()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LOCKER_NOT_AVAILABLE));

        availableLocker.setLockerStatus(false);
        lockerRepository.save(availableLocker);

        product.setLocker(availableLocker);
        productRepository.save(product);

        // 거래 내역 생성
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        Trade trade = new Trade()
                .setProduct(product)
                .setBuyer(buyer)
                .setSeller(product.getSeller())
                .setLocker(availableLocker);

        buyer.addPurchasedProduct(product);

        return tradeRepository.save(trade);
    }

    // 구매자 구매 내역 관리
    public List<Product> findProductsByBuyer(Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        return productRepository.findByBuyer(buyer);
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}