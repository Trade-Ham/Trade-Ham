package com.example.shoppingmallproject.purchase.service;

import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.service.LockerService;
import com.example.shoppingmallproject.login.domain.User;
import com.example.shoppingmallproject.login.repository.UserRepository;
import com.example.shoppingmallproject.login.security.JwtTokenProvider;
import com.example.shoppingmallproject.purchase.repository.PurchaseRepository;
import com.example.shoppingmallproject.sell.domain.Products;
import com.example.shoppingmallproject.sell.domain.StatusType;
import com.example.shoppingmallproject.trade.domain.Trades;
import com.example.shoppingmallproject.trade.repository.TradeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final LockerService lockerService;
    private final TradeRepository tradeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * 구매자가 물건 구매를 시작함 (Pessimistic Lock 적용)
     */
    @Transactional
    public boolean startPurchase(Long productId, HttpServletRequest request) {
        // Pessimistic Lock으로 상품 데이터 조회
        Products product = purchaseRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Long buyerId = jwtTokenProvider.checkTokenValidity(request);

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 상품 상태가 SELL이 아닌 경우 구매 시작 불가
        if (!product.getStatus().equals(StatusType.SELL)) {
            return false;
        }

        // 상품 상태를 CHECK로 업데이트하고 구매자에게 판매자 정보 알림 전달
        product.setStatus(StatusType.CHECK);
        purchaseRepository.save(product);

        // Trade 엔티티 생성
        Trades trade = new Trades();
        trade.setProduct(product);
        trade.setBuyer(buyer);
        trade.setSeller(product.getSeller());
        trade.setCreatedAt(LocalDateTime.now());
        trade.setUpdatedAt(LocalDateTime.now());

        tradeRepository.save(trade);

        // 판매자 실명과 계좌번호를 알림으로 전달 (예: 알림 DB 저장)
        // sendNotificationToBuyer(product.getSellerInfo());

        return true;
    }

    /**
     * 구매자가 입금 후 구매 완료 버튼 클릭
     */
    @Transactional
    public boolean completePurchase(Long productId) {
        Products product = purchaseRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 상품 상태가 CHECK가 아닌 경우 구매 완료 불가
        if (!product.getStatus().equals(StatusType.CHECK)) {
            return false;
        }

        // 사물함 아이디 및 비밀번호를 알림으로 전달 (DB 저장)
        // sendLockerInfoToSeller(product.getLockerInfo());

        return true;
    }

    /**
     * 판매자가 물건을 사물함에 넣고 확인 버튼 클릭
     */
    @Transactional
    public boolean storeInLocker(Long productId) {
        Products product = purchaseRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 상품 상태가 CHECK가 아닌 경우 사물함에 저장 불가
        if (!product.getStatus().equals(StatusType.CHECK)) {
            return false;
        }

        // 상품 상태를 WAIT로 업데이트하고 사물함 상태를 False로 변경
        product.setStatus(StatusType.WAIT);
        Locker locker = lockerService.assignLocker();
        purchaseRepository.save(product);

        // Trade 엔티티 업데이트
        Trades trade = tradeRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Trade not found"));
        trade.setLocker(locker);
        trade.setUpdatedAt(LocalDateTime.now());

        tradeRepository.save(trade);

        // 구매자에게 사물함 정보 알림
        // sendNotificationToBuyer(product.getLockerInfo());

        return true;
    }

    /**
     * 구매자가 물건 수령 완료 버튼 클릭
     */
    @Transactional
    public boolean markAsReceived(Long productId) {
        // Pessimistic Lock으로 상품 데이터 조회
        Products product = purchaseRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 상품 상태가 WAIT가 아닌 경우 수령 완료 불가
        if (!product.getStatus().equals(StatusType.WAIT)) {
            return false;
        }

        product.setStatus(StatusType.DONE);
        purchaseRepository.save(product);

        // Trade 엔티티 업데이트
        Trades trade = tradeRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Trade not found"));
        trade.setUpdatedAt(LocalDateTime.now());

        lockerService.unlockLocker(trade.getLocker().getLockerId());

        tradeRepository.save(trade);

        return true;
    }
}
