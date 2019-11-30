package com.foodtaffic.menu.service;

import com.foodtaffic.menu.entity.Menu;
import com.foodtaffic.menu.entity.MenuItem;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;

public interface MenuService {

	MenuDto getMenuByFoodTruck(Long foodTruckId);

	MenuDto createMenu(Long foodTruckId, Menu menu, String accessToken);

	MenuItemDto createMenuItem(Long foodTruckId, Long menuId, MenuItem menuItem, String accessToken);

	MenuDto updateMenu(Long foodTruckId, Long menuId, Menu menu, String accessToken);

	MenuItemDto updateMenuItem(Long foodTruckId, Long menuId, Long menuItemId, MenuItem menuItem, String accessToken);

}
