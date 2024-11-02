package com.example.shoppingmallproject.purchase.controller;

import com.example.shoppingmallproject.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * 구매자는 물건 상세 설명을 확인하고 구매 버튼을 클릭한다
     */
    @GetMapping("/purchase-page/{product_id}")
    public ResponseEntity<String> startPurchase(@PathVariable Long product_id) {
        boolean success = purchaseService.startPurchase(product_id);
        return success ? ResponseEntity.ok("Purchase started successfully.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to start purchase.");
    }

    /**
     * 구매자는 입금 후 구매 완료 버튼을 클릭한다
     */
    @GetMapping("/purchase/{product_id}")
    public ResponseEntity<String> donePurchase(@PathVariable Long product_id) {
        boolean success = purchaseService.completePurchase(product_id);
        return success ? ResponseEntity.ok("Purchase completed successfully.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to complete purchase.");
    }

    /**
     * 판매자는 물건을 사물함에 넣고 확인 버튼을 클릭한다
     */
    @GetMapping("/locker-in/{product_id}")
    public ResponseEntity<String> storeInLocker(@PathVariable Long product_id) {
        boolean success = purchaseService.storeInLocker(product_id);
        return success ? ResponseEntity.ok("Product stored in locker.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to store product in locker.");
    }

    /**
     * 구매자는 물건 수령 후 완료 버튼을 클릭한다
     */
    @GetMapping("/take-out/{product_id}")
    public ResponseEntity<String> markAsReceived(@PathVariable Long product_id) {
        boolean success = purchaseService.markAsReceived(product_id);
        return success ? ResponseEntity.ok("Product received successfully.")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to mark product as received.");
    }
}
