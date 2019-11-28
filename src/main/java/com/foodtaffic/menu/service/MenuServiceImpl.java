package com.foodtaffic.menu.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.foodtaffic.menu.entity.Menu;
import com.foodtaffic.menu.repository.MenuRepository;
import com.foodtraffic.client.FoodTruckClient;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuRepository menuRepo;
	
	@Autowired
	private FoodTruckClient foodTruckClient;
	
	public Menu getMenuByFoodTruck(Long foodTruckId) {
		if(foodTruckClient.checkFoodTruck(null, foodTruckId)) {
			Optional<Menu> menu = menuRepo.findByFoodTruckId(foodTruckId);
			return menu.get();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food Truck " + foodTruckId + " does not exist");
		}
	}

}
