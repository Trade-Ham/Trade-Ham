package com.trade_ham.domain.product.repository;

import com.trade_ham.domain.product.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {}
