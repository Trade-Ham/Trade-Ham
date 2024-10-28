package com.example.shoppingmallproject.product.repository;

import com.example.shoppingmallproject.product.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {}
