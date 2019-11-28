package com.foodtaffic.menu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodtaffic.menu.entity.Menu;
import com.foodtaffic.menu.service.MenuService;

import io.swagger.annotations.Api;

@Api(tags = "Menu")
@RestController
@RequestMapping("/food-trucks/{foodTruckId}/menus")
public class MenuController {

	@Autowired
	private MenuService menuService;
	
	@GetMapping
	public Menu getMenuForFoodTruck(@PathVariable(name = "foodTruckId") Long foodTruckId) {
		return menuService.getMenuByFoodTruck(foodTruckId);
	}
}
