package com.foodtaffic.menu.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.foodtaffic.menu.entity.Menu;
import com.foodtaffic.menu.entity.MenuItem;
import com.foodtaffic.menu.repository.MenuItemRepository;
import com.foodtaffic.menu.repository.MenuRepository;
import com.foodtraffic.client.EmployeeClient;
import com.foodtraffic.client.FoodTruckClient;
import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;
import com.foodtraffic.model.dto.UserDto;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuRepository menuRepo;
	
	@Autowired
	private MenuItemRepository menuItemRepo;

	@Autowired
	private FoodTruckClient foodTruckClient;

	@Autowired
	private UserClient userClient;

	@Autowired
	private EmployeeClient employeeClient;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public MenuDto getMenuByFoodTruck(Long foodTruckId) {
		if(foodTruckClient.checkFoodTruck(null, foodTruckId)) {
			Optional<Menu> menu = menuRepo.findByFoodTruckId(foodTruckId);
			return modelMapper.map(menu.get(), MenuDto.class);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Food Truck does not exist");
		}
	}

	@Override
	public MenuDto createMenu(Long foodTruckId, Menu menu, String accessToken) {
		HttpStatus status;
		String message;
		
		if (!foodTruckClient.checkFoodTruck(null, foodTruckId)) {
			message = "Food Truck does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!isUserAnOwnerOrAdmin(foodTruckId, accessToken)) {
			message = "Must be an owner or admin to perform this operation";
			status = HttpStatus.FORBIDDEN;
		} else {
			menu.setId(0L);
			menu.setFoodTruckId(foodTruckId);
			menu = menuRepo.save(menu);
			return modelMapper.map(menu, MenuDto.class);
		}

		throw new ResponseStatusException(status, message);
	}
	
	@Override
	public MenuItemDto createMenuItem(Long foodTruckId, Long menuId, MenuItem menuItem, String accessToken) {
		HttpStatus status;
		String message;
		
		if (!foodTruckClient.checkFoodTruck(null, foodTruckId)) {
			message = "Food Truck does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!menuRepo.existsById(menuId)) {
			message = "Menu does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!isUserAnOwnerOrAdmin(foodTruckId, accessToken)) {
			message = "Must be an owner or admin to perform this operation";
			status = HttpStatus.FORBIDDEN;
		} else {
			menuItem.setId(0L);
			menuItem.setMenuId(menuId);
			menuItem = menuItemRepo.save(menuItem);
			return modelMapper.map(menuItem, MenuItemDto.class);
		}
		
		throw new ResponseStatusException(status, message);
	}
	
	@Override
	public MenuDto updateMenu(Long foodTruckId, Long menuId, Menu menu, String accessToken) {
		HttpStatus status;
		String message;
		
		if (!menuRepo.existsByIdAndFoodTruckId(menuId, foodTruckId)) {
			message = "Resource does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!isUserAnOwnerOrAdmin(foodTruckId, accessToken)) {
			message = "Must be an owner or admin to perform this operation";
			status = HttpStatus.FORBIDDEN;
		} else {
			menu.setId(menuId);
			menu.setFoodTruckId(foodTruckId);
			menu = menuRepo.save(menu);
			return modelMapper.map(menu, MenuDto.class);
		}

		throw new ResponseStatusException(status, message);
	}
	
	@Override
	public MenuItemDto updateMenuItem(Long foodTruckId, Long menuId, Long menuItemId, MenuItem menuItem,
			String accessToken) {
		HttpStatus status;
		String message;
		
		if (!menuRepo.existsByIdAndFoodTruckId(menuId, foodTruckId) || !menuItemRepo.existsByIdAndMenuId(menuItemId, menuId)) {
			message = "Resource does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!isUserAnOwnerOrAdmin(foodTruckId, accessToken)) {
			message = "Must be an owner or admin to perform this operation";
			status = HttpStatus.FORBIDDEN;
		} else {
			menuItem = mergeMenuItem(menuItem, menuItemId);
			menuItem.setMenuId(menuId);
			menuItem.setId(menuItemId);
			menuItem = menuItemRepo.save(menuItem);
			return modelMapper.map(menuItem, MenuItemDto.class);
		}

		throw new ResponseStatusException(status, message);
	}
	
	@Override
	public void deleteMenu(Long foodTruckId, Long menuId, String accessToken) {
		HttpStatus status;
		String message;
		
		if (!menuRepo.existsByIdAndFoodTruckId(menuId, foodTruckId)) {
			message = "Resource does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!isUserAnOwnerOrAdmin(foodTruckId, accessToken)) {
			message = "Must be an owner or admin to perform this operation";
			status = HttpStatus.FORBIDDEN;
		} else {
			menuRepo.deleteById(menuId);
			return;
		}
		
		throw new ResponseStatusException(status, message);
	}
	
	@Override
	public void deleteMenuItem(Long foodTruckId, Long menuId, Long menuItemId, String accessToken) {
		HttpStatus status;
		String message;
		
		if (!menuRepo.existsByIdAndFoodTruckId(menuId, foodTruckId) || !menuItemRepo.existsByIdAndMenuId(menuItemId, menuId)) {
			message = "Resource does not exist";
			status = HttpStatus.NOT_FOUND;
		} else if (!isUserAnOwnerOrAdmin(foodTruckId, accessToken)) {
			message = "Must be an owner or admin to perform this operation";
			status = HttpStatus.FORBIDDEN;
		} else {
			menuItemRepo.deleteById(menuItemId);
			return;
		}
		throw new ResponseStatusException(status, message);
	}
	
	private boolean isUserAnOwnerOrAdmin(Long foodTruckId, String accessToken) {
		List<EmployeeDto> employees = employeeClient.getEmployeesByFoodTruck(foodTruckId, "admin");
		UserDto user = userClient.checkAccessHeader(accessToken);
		
		for (EmployeeDto emp : employees)
			if (emp.getUserId() == user.getId())
				return true;

		return false;
	}
	
	private MenuItem mergeMenuItem(MenuItem updatedItem, Long menuItemId) {
		MenuItem mergedItem = menuItemRepo.getById(menuItemId);
		
		try {
			for (Field f : updatedItem.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if(f.get(updatedItem) != null) {
					f.set(mergedItem, f.get(updatedItem));
				}
			}
		} catch (IllegalAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return mergedItem;
	}

}
