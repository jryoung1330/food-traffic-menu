package com.foodtraffic.menu.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.menu.entity.Menu;
import com.foodtraffic.menu.entity.MenuItem;
import com.foodtraffic.menu.repository.MenuItemRepository;
import com.foodtraffic.menu.repository.MenuRepository;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.MenuItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.util.AppUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuRepository menuRepo;
	
	@Autowired
	private MenuItemRepository menuItemRepo;

	@Autowired
	private UserClient userClient;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<MenuDto> getAllMenusByVendor(final long vendorId) {
		List<Menu> menus = menuRepo.findAllByVendorIdOrderByDisplayOrder(vendorId);
		return modelMapper.map(menus, new TypeToken<List<MenuDto>>(){}.getType());
	}

	@Override
	public MenuDto createMenu(final long vendorId, @Valid Menu menu, final String accessToken) {
		if (!isAdmin(vendorId, accessToken)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient privileges");
		}

		menu.setId(0L);
		menu.setVendorId(vendorId);
		menu.setDisplayOrder(menuRepo.countAllByVendorId(vendorId));
		menu = menuRepo.save(menu);
		return modelMapper.map(menu, MenuDto.class);
	}

	@Override
	public MenuDto updateMenu(final long vendorId, final long menuId, @Valid Menu menu, final String accessToken) {
		validateRequest(menuRepo.existsByIdAndVendorId(menuId, vendorId), vendorId, accessToken);

		Integer displayOrder = menu.getDisplayOrder();
		menu.setDisplayOrder(null);

		menu = (Menu) AppUtil.mergeObject(menuRepo, menu, menuId);
		menu.setId(menuId);
		menu.setVendorId(vendorId);
		menu = menuRepo.save(menu);

		// display order changed
		if(displayOrder != null && !displayOrder.equals(menu.getDisplayOrder())) {
			List<Menu> renumberedMenus = updateDisplayOrder(menuId, vendorId, displayOrder);
			menuRepo.saveAll(renumberedMenus);
		}

		return modelMapper.map(menu, MenuDto.class);
	}

	@Override
	public void deleteMenu(final long vendorId, final long menuId, final String accessToken) {
		validateRequest(menuRepo.existsByIdAndVendorId(menuId, vendorId), vendorId, accessToken);
		menuRepo.deleteById(menuId);

		// renumber the menus that are left
		List<Menu> menus = menuRepo.findAllByVendorIdOrderByDisplayOrder(vendorId);
		setDisplayOrders(menus);
		menuRepo.saveAll(menus);
	}

	@Override
	public MenuItemDto createMenuItem(final long vendorId, final long menuId, @Valid MenuItem menuItem, final String accessToken) {
		validateRequest(menuRepo.existsById(menuId), vendorId, accessToken);

		menuItem.setId(0L);
		menuItem.setMenuId(menuId);
		menuItem = menuItemRepo.save(menuItem);
		return modelMapper.map(menuItem, MenuItemDto.class);
	}

	@Override
	public MenuItemDto updateMenuItem(final long vendorId, final long menuId, final long menuItemId, @Valid MenuItem menuItem,
									  final String accessToken) {
		validateRequest(menuItemRepo.existsByIdAndMenuId(menuItemId, menuId), vendorId, accessToken);

		menuItem = (MenuItem) AppUtil.mergeObject(menuItemRepo, menuItem, menuItemId);
		menuItem.setMenuId(menuId);
		menuItem.setId(menuItemId);
		menuItem = menuItemRepo.save(menuItem);
		return modelMapper.map(menuItem, MenuItemDto.class);
	}

	@Override
	public void deleteMenuItem(final long vendorId, final long menuId, final long menuItemId, final String accessToken) {
		validateRequest(menuItemRepo.existsByIdAndMenuId(menuItemId, menuId), vendorId, accessToken);
		menuItemRepo.deleteById(menuItemId);
	}
	
	@Override
	public List<MenuItemDto> getTopSellingItems(Long vendorId, String accessToken) {
		// TODO: revise when order data is available
		List<MenuItem> menuItems = menuItemRepo.findAllByVendorId(vendorId);
		List<MenuItem> topThree = new ArrayList<>();
		for(int i=0; i<3 && i<menuItems.size(); i++) {
			topThree.add(menuItems.get(i));
		}
		return modelMapper.map(topThree, new TypeToken<List<MenuItemDto>>(){}.getType());
	}

	/*
	 * helper methods
	 */

	private boolean isAdmin(Long vendorId, String accessToken) {
		UserDto user = AppUtil.getUser(userClient, accessToken);
		EmployeeDto emp = user.getEmployee();
		return emp != null && emp.getVendorId() == vendorId && emp.isAdmin();
	}

	private void validateRequest(boolean resourcesExist, long vendorId, String accessToken) {
		if (!resourcesExist) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
		} else if (!isAdmin(vendorId, accessToken)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient privileges");
		}
	}

	public List<Menu> updateDisplayOrder(long id, long vendorId, int displayOrder) {
		List<Menu> menus = menuRepo.findAllByVendorIdOrderByDisplayOrder(vendorId);

		if(displayOrder != menus.size()-1 && menus.get(displayOrder).getId() != id && menus.get(displayOrder+1).getId() == id) {
			swap(menus, displayOrder, displayOrder+1);
		} else if(displayOrder != 0 && menus.get(displayOrder).getId() != id && menus.get(displayOrder-1).getId() == id) {
			swap(menus, displayOrder, displayOrder-1);
		}

		setDisplayOrders(menus);

		return menus;
	}

	private void setDisplayOrders(List<Menu> menus) {
		for(int i=0; i<menus.size(); i++) {
			menus.get(i).setDisplayOrder(i);
		}
	}

	private void swap(List<Menu> menus, int i, int j) {
		Menu temp = menus.get(j);
		menus.set(j, menus.get(i));
		menus.set(i, temp);
	}
}
