package com.foodtraffic.menu.service;

import com.foodtraffic.menu.entity.Menu;
import com.foodtraffic.menu.entity.MenuItem;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;

import javax.validation.Valid;
import java.util.List;

public interface MenuService {

	List<MenuDto> getAllMenusByVendor(long vendorId);

	MenuDto createMenu(long vendorId, @Valid Menu menu, String accessToken);

	MenuDto updateMenu(long vendorId, long menuId, @Valid Menu menu, String accessToken);

	void deleteMenu(long vendorId, long menuId, String accessToken);

	MenuItemDto createMenuItem(long vendorId, long menuId, MenuItem menuItem, String accessToken);

	MenuItemDto updateMenuItem(long vendorId, long menuId, long menuItemId, MenuItem menuItem, String accessToken);

	void deleteMenuItem(long vendorId, long menuId, long menuItemId, String accessToken);

	List<MenuItemDto> getTopSellingItems(Long vendorId, String accessToken);

}
