package com.winestoreapp.repository;

import com.winestoreapp.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine,Long> {
}
