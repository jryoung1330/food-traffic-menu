package com.foodtraffic.menu.repository;

import com.foodtraffic.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    boolean existsByIdAndVendorId(Long id, Long vendorId);

    List<Menu> findAllByVendorId(Long vendorId);
}