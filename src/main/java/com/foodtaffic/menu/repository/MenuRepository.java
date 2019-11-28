package com.foodtaffic.menu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodtaffic.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long>{

	Optional<Menu> findByFoodTruckId(Long foodTruckId);
}
