package com.trade_ham.domain.product.service;

import com.trade_ham.domain.auth.entity.UserEntity;
import com.trade_ham.domain.auth.repository.UserRepository;
import com.trade_ham.domain.locker.entity.LockerEntity;
import com.trade_ham.domain.locker.repository.LockerRepository;
import com.trade_ham.domain.notifiaction.entity.NotificationEntity;
import com.trade_ham.domain.notifiaction.repository.NotificationRepository;
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

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PurchaseProductService {
    private final ProductRepository productRepository;
    private final LockerRepository lockerRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final NotificationRepository notificationRepository;

    /*
    사용자가 구매 요청 버튼 클릭
    -> 해당 물품에 락 걸기
    -> 물품 상태 변경
     */
    @Transactional
    public ProductEntity purchaseProduct(Long productId) {
        // 동시성을 고려해 비관적 락을 사용
        ProductEntity productEntity = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!productEntity.getStatus().equals(ProductStatus.SELL)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }

        // ProductEntity 상태를 'CHECK'로 변경
        productEntity.setStatus(ProductStatus.CHECK);
        productRepository.save(productEntity);

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
        LockerEntity availableLockerEntity = assignAvailableLocker(productEntity);

        // 거래 내역 생성
        TradeEntity tradeEntity = createTradeHistory(buyerId, productEntity, availableLockerEntity);

        // 판매자에게 사물함 ID와 비밀번호를 전달
        sendLockerInfoToSeller(productEntity.getSeller(), availableLockerEntity);

        return tradeRepository.save(tradeEntity);
    }

    private void sendLockerInfoToSeller(UserEntity seller, LockerEntity availableLockerEntity) {
        NotificationEntity notification = NotificationEntity.createNotification(
                seller,
                availableLockerEntity.getLockerNumber(),
                availableLockerEntity.getLockerPassword());
        notificationRepository.save(notification);
    }

    private TradeEntity createTradeHistory(Long buyerId, ProductEntity productEntity, LockerEntity availableLockerEntity) {
        UserEntity buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        TradeEntity tradeEntity = TradeEntity.builder()
                .productEntity(productEntity)
                .buyer(buyer)
                .seller(productEntity.getSeller())
                .lockerEntity(availableLockerEntity)
                .build();

        buyer.addPurchasedProduct(productEntity);
        return tradeEntity;
    }

    private LockerEntity assignAvailableLocker(ProductEntity productEntity) {
        LockerEntity availableLockerEntity = lockerRepository.findFirstByLockerStatusTrue()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LOCKER_NOT_AVAILABLE));

        availableLockerEntity.setLockerStatus(false);
        //랜덤한 비밀번호를 생성
        availableLockerEntity.setLockerPassword(generateRandomPassword());
        lockerRepository.save(availableLockerEntity);

        productEntity.setLockerEntity(availableLockerEntity);
        productRepository.save(productEntity);
        return availableLockerEntity;
    }

    public String generateRandomPassword() {
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); // 1000~9999 범위의 숫자 생성
        return String.valueOf(randomNumber); // 숫자를 String으로 변환
    }

    public ProductEntity findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}