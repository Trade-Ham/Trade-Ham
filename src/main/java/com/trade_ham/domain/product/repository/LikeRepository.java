package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.product.entity.LikeEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    // 상품별 좋아요 개수 조회
    // 다른 사용자가 좋아요를 누를 때 값이 변경되므로 동시성 처리를 위해 락 사용
    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.product.id = :productId")
    @Lock(LockModeType.PESSIMISTIC_READ)
    Long countByProductId(@Param("productId") Long productId);

    // 특정 유저의 좋아요 여부 확인
    Optional<LikeEntity> findByUserIdAndProductId(Long userId, Long productId);

    // 좋아요 개수 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE Like l SET l.likeCount = :likeCount WHERE 1.product.id = :productId")
    void updateLikeCount(@Param("productId") Long productId, @Param("likeCount") Long likeCount);

    // 특정 유저가 좋아요한 상품 목록
    // N+1 고려하여 fetch join 사용
    @Query("SELECT l FROM LikeEntity l JOIN FETCH l.user u JOIN FETCH l.product p WHERE u.id = :userId")
    List<LikeEntity> findByUserId(@Param("userId") Long userId);
}
