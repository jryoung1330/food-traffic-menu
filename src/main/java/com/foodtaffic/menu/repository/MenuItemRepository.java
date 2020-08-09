package com.foodtaffic.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtaffic.menu.entity.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>{

	MenuItem getById(Long menuItemId);
	boolean existsByIdAndMenuId(Long id, Long menuId);

}
