package com.trade_ham.domain.product.service;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.locker.entity.LockerEntity;
import com.trade_ham.domain.locker.repository.LockerRepository;
import com.trade_ham.domain.notification.service.NotificationService;
import com.trade_ham.domain.product.entity.ProductEntity;
import com.trade_ham.domain.product.entity.ProductStatus;
import com.trade_ham.domain.product.entity.TradeEntity;
import com.trade_ham.domain.product.repository.ProductRepository;
import com.trade_ham.domain.product.repository.TradeRepository;
import com.trade_ham.global.common.exception.AccessDeniedException;
import com.trade_ham.global.common.exception.ErrorCode;
import com.trade_ham.global.common.exception.InvalidProductStateException;
import com.trade_ham.global.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseProductService {
    private final ProductRepository productRepository;
    private final LockerRepository lockerRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final NotificationService notificationService;

    /*
    사용자가 구매 요청 버튼 클릭
    -> 해당 물품에 락 걸기
    -> 물품 상태 변경
     */
    @Transactional
    public ProductEntity purchaseProduct(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // 상태가 SELL이 아니라면 예외 발생
        if (!productEntity.getStatus().equals(ProductStatus.SELL)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        productEntity = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));


        // ProductEntity 상태를 'CHECK'로 변경
        productEntity.setStatus(ProductStatus.CHECK);

        return productEntity;
    }

    /*
     구매 완료 버튼 클릭
     물품 상태 변경
     물품에 사물함을 할당하고 사물함 상태 변경
     (추후 판매자에게 알림을 보내주는 서비스 구현)
     거래 내역 생성
     */
    @Transactional
    public TradeEntity completePurchase(Long productId, Long buyerId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!productEntity.getStatus().equals(ProductStatus.CHECK)) {
            throw new InvalidProductStateException(ErrorCode.INVALID_PRODUCT_STATE);
        }

        // 상태를 WAIT으로 변경
        productEntity.setStatus(ProductStatus.WAIT);

        // 사용 가능한 사물함 할당
        LockerEntity availableLockerEntity = lockerRepository.findFirstByLockerStatusTrue()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LOCKER_NOT_AVAILABLE));

        availableLockerEntity.setLockerStatus(false);
        lockerRepository.save(availableLockerEntity);

        productEntity.setLockerEntity(availableLockerEntity);
        productRepository.save(productEntity);

        // 거래 내역 생성
        UserEntity buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        TradeEntity tradeEntity = TradeEntity.builder()
                .productEntity(productEntity)
                .buyer(buyer)
                .seller(productEntity.getSeller())
                .lockerEntity(availableLockerEntity)
                .build();

        buyer.addPurchasedProduct(productEntity);


        // 판매자에게 알림 생성
        notificationService.createLockerNotification(
                productEntity.getSeller(),
                availableLockerEntity.getLockerNumber(),
                availableLockerEntity.getLockerPassword()
        );

        // 구매자에게 구매 완료 알림
        notificationService.createPurchaseCompleteNotification(
                buyer,
                productEntity.getName()
        );

        return tradeRepository.save(tradeEntity);
    }



    public ProductEntity findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}