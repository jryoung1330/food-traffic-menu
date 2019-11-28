package com.foodtaffic.menu.service;

import com.foodtaffic.menu.entity.Menu;

public interface MenuService {

	Menu getMenuByFoodTruck(Long foodTruckId);

}
