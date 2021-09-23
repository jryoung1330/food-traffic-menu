package com.foodtraffic.menu.controller;

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

import com.foodtraffic.menu.entity.Menu;
import com.foodtraffic.menu.entity.MenuItem;
import com.foodtraffic.menu.service.MenuService;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;

import io.swagger.annotations.Api;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@Api(tags = "Menu")
@RestController
@RequestMapping("/vendors/{vendorId}/menus")
public class MenuController {

	@Autowired
	private MenuService menuService;

	@GetMapping
	public List<MenuDto> getMenusForVendor(@PathVariable(name = "vendorId") Long vendorId) {
		return menuService.getAllMenusByVendor(vendorId);
	}

	@GetMapping("/menu-items/top-sellers")
	public List<MenuItemDto> getTopSellers(@PathVariable(name = "vendorId") Long vendorId,
										   @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.getTopSellingItems(vendorId, accessToken);
	}

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public MenuDto createMenu(@PathVariable(name = "vendorId") Long vendorId,
							  @RequestBody Menu menu,
							  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.createMenu(vendorId, menu, accessToken);
	}

	@PostMapping("/{menuId}/menu-items")
	@ResponseStatus(code = HttpStatus.CREATED)
	public MenuItemDto createMenuItem(@PathVariable(name = "vendorId") Long vendorId,
									  @PathVariable(name = "menuId") Long menuId,
									  @RequestBody MenuItem menuItem,
									  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.createMenuItem(vendorId, menuId, menuItem, accessToken);
	}

	@PutMapping("/{menuId}")
	public MenuDto createMenu(@PathVariable(name = "vendorId") Long vendorId,
							  @PathVariable(name = "menuId") Long menuId,
							  @RequestBody Menu menu,
							  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.updateMenu(vendorId, menuId, menu, accessToken);
	}

	@PutMapping("/{menuId}/menu-items/{menuItemId}")
	public MenuItemDto updateMenuItem(@PathVariable(name = "vendorId") Long vendorId,
									  @PathVariable(name = "menuId") Long menuId,
									  @PathVariable(name = "menuItemId") Long menuItemId,
									  @RequestBody MenuItem menuItem,
									  @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		return menuService.updateMenuItem(vendorId, menuId, menuItemId, menuItem, accessToken);
	}

	@DeleteMapping("/{menuId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenu(@PathVariable(name = "vendorId") Long vendorId,
						   @PathVariable(name = "menuId") Long menuId,
						   @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		menuService.deleteMenu(vendorId, menuId, accessToken);
	}

	@DeleteMapping("/{menuId}/menu-items/{menuItemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenuItem(@PathVariable(name = "vendorId") Long vendorId,
							   @PathVariable(name = "menuId") Long menuId,
							   @PathVariable(name = "menuItemId") Long menuItemId,
							   @CookieValue(name = "_gid", defaultValue = "_gid") String accessToken) {
		menuService.deleteMenuItem(vendorId, menuId, menuItemId, accessToken);
	}
	
}
