package com.example.shoppingmallproject.product.service;

import com.example.shoppingmallproject.common.exception.AccessDeniedException;
import com.example.shoppingmallproject.common.exception.InvalidProductStateException;
import com.example.shoppingmallproject.common.exception.ResourceNotFoundException;
import com.example.shoppingmallproject.locker.domain.Locker;
import com.example.shoppingmallproject.locker.repository.LockerRepository;
import com.example.shoppingmallproject.product.domain.Product;
import com.example.shoppingmallproject.product.domain.ProductStatus;
import com.example.shoppingmallproject.product.domain.Trade;
import com.example.shoppingmallproject.product.repository.ProductRepository;
import com.example.shoppingmallproject.product.repository.TradeRepository;
import com.example.shoppingmallproject.user.domain.User;
import com.example.shoppingmallproject.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private LockerRepository lockerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private PurchaseProductService purchaseProductService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 상품이 정상적으로 구매 프로세스에 진입할 때의 동작 테스트
    @Test
    void testPurchaseProduct_Success() {
        Long productId = 1L;
        Product product = new Product();
        product.setStatus(ProductStatus.SELL);

        when(productRepository.findByIdWithPessimisticLock(productId)).thenReturn(Optional.of(product));

        Product result = purchaseProductService.purchaseProduct(productId);

        assertEquals(ProductStatus.CHECK, result.getStatus());
        verify(productRepository, times(1)).save(product);
    }

    // 상품이 존재하지 않는 경우, ResourceNotFoundException 예외를 발생시키는지 테스트
    @Test
    void testPurchaseProduct_ProductNotFound() {
        Long productId = 1L;
        when(productRepository.findByIdWithPessimisticLock(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseProductService.purchaseProduct(productId));
    }

    // 구매 완료 후 거래 내역이 정상적으로 생성되고 상태가 변경되는지 테스트
    @Test
    void testCompletePurchase_Success() {
        Long productId = 1L;
        Long buyerId = 2L;

        Product product = new Product();
        product.setStatus(ProductStatus.CHECK);

        Locker locker = new Locker();
        locker.setLockerStatus(true);

        User buyer = new User();
        Trade trade = new Trade().setProduct(product).setBuyer(buyer).setLocker(locker);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(lockerRepository.findFirstByLockerStatusTrue()).thenReturn(Optional.of(locker));
        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        Trade result = purchaseProductService.completePurchase(productId, buyerId);

        assertNotNull(result);
        assertEquals(product, result.getProduct());
        assertEquals(buyer, result.getBuyer());
        assertEquals(locker, result.getLocker());
        assertEquals(ProductStatus.WAIT, product.getStatus());
    }

    // 구매 완료 시 상품 상태가 CHECK가 아닌 경우 InvalidProductStateException 예외 발생을 테스트
    @Test
    void testCompletePurchase_ProductInvalidState() {
        Long productId = 1L;
        Product product = new Product();
        product.setStatus(ProductStatus.SELL);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(InvalidProductStateException.class, () -> purchaseProductService.completePurchase(productId, 1L));
    }

    // 상품 상태가 SELL이 아닌 경우 AccessDeniedException 예외 발생을 테스트
    @Test
    void testPurchaseProduct_ProductNotForSale() {
        Long productId = 1L;
        Product product = new Product();
        product.setStatus(ProductStatus.DONE);

        when(productRepository.findByIdWithPessimisticLock(productId)).thenReturn(Optional.of(product));

        assertThrows(AccessDeniedException.class, () -> purchaseProductService.purchaseProduct(productId));
    }

    // 구매 완료 시 사용 가능한 사물함이 없을 경우 ResourceNotFoundException 예외 발생을 테스트
    @Test
    void testCompletePurchase_NoAvailableLocker() {
        Long productId = 1L;
        Long buyerId = 2L;
        Product product = new Product();
        product.setStatus(ProductStatus.CHECK);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(lockerRepository.findFirstByLockerStatusTrue()).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseProductService.completePurchase(productId, buyerId));
    }

    // 구매 완료 시 구매자가 존재하지 않는 경우 ResourceNotFoundException 예외 발생을 테스트
    @Test
    void testCompletePurchase_BuyerNotFound() {
        Long productId = 1L;
        Long buyerId = 2L;
        Product product = new Product();
        product.setStatus(ProductStatus.CHECK);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(lockerRepository.findFirstByLockerStatusTrue()).thenReturn(Optional.of(new Locker()));
        when(userRepository.findById(buyerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseProductService.completePurchase(productId, buyerId));
    }
}