package com.manuel.ecommerce.repositories;

import com.manuel.ecommerce.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Page<Category> findByNameStartingWith(String name, Pageable pageable);
}
