package com.foodtaffic.menu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodtaffic.menu.entity.Menu;
import com.foodtaffic.menu.entity.MenuItem;
import com.foodtaffic.menu.service.MenuService;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;

import io.swagger.annotations.Api;

@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.66:3000"})
@Api(tags = "Menu")
@RestController
@RequestMapping("/food-trucks/{foodTruckId}/menus")
public class MenuController {

	@Autowired
	private MenuService menuService;
	
	@GetMapping
	public MenuDto getMenuForFoodTruck(@PathVariable(name = "foodTruckId") Long foodTruckId) {
		return menuService.getMenuByFoodTruck(foodTruckId);
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public MenuDto createMenu(@PathVariable(name = "foodTruckId") Long foodTruckId, 
							  @RequestBody Menu menu,
							  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.createMenu(foodTruckId, menu, accessToken);
	}
	
	@PostMapping("/{menuId}/menu-items")
	@ResponseStatus(code = HttpStatus.CREATED)
	public MenuItemDto createMenuItem(@PathVariable(name = "foodTruckId") Long foodTruckId,
									  @PathVariable(name = "menuId") Long menuId,
									  @RequestBody MenuItem menuItem,
									  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.createMenuItem(foodTruckId, menuId, menuItem, accessToken);
	}
	
	@PutMapping("/{menuId}")
	public MenuDto createMenu(@PathVariable(name = "foodTruckId") Long foodTruckId,
							  @PathVariable(name = "menuId") Long menuId,
							  @RequestBody Menu menu,
							  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.updateMenu(foodTruckId, menuId, menu, accessToken);
	}
	
	@PutMapping("/{menuId}/menu-items/{menuItemId}")
	public MenuItemDto updateMenuItem(@PathVariable(name = "foodTruckId") Long foodTruckId,
							  @PathVariable(name = "menuId") Long menuId,
							  @PathVariable(name = "menuItemId") Long menuItemId,
							  @RequestBody MenuItem menuItem,
							  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.updateMenuItem(foodTruckId, menuId, menuItemId, menuItem, accessToken);
	}
	
	@DeleteMapping("/{menuId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenu(@PathVariable(name = "foodTruckId") Long foodTruckId,
							  @PathVariable(name = "menuId") Long menuId,
							  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		menuService.deleteMenu(foodTruckId, menuId, accessToken);
	}
	
	@DeleteMapping("/{menuId}/menu-items/{menuItemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenuItem(@PathVariable(name = "foodTruckId") Long foodTruckId,
							   @PathVariable(name = "menuId") Long menuId,
							   @PathVariable(name = "menuItemId") Long menuItemId,
							   @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		menuService.deleteMenuItem(foodTruckId, menuId, menuItemId, accessToken);
	}
	
}
