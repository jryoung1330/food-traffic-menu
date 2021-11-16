package com.foodtraffic.menu.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.menu.entity.Menu;
import com.foodtraffic.menu.repository.MenuRepository;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    MenuRepository menuRepo;

    @Mock
    UserClient userClient;

    @Spy
    ModelMapper modelMapper;

    @InjectMocks
    MenuServiceImpl menuService;

    private Menu mockMenu;

    private UserDto mockUser;

    private static final String ACCESS_TOKEN = "test";

    @BeforeEach
    public void setup() {
        mockMenu = mockMenu();
        mockUser = mockUser();
    }

    @Test
    public void givenAssociate_whenCreateMenu_throw403Exception() {
        UserDto user = mockUser;
        user.getEmployee().setAdmin(false);

        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(user);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> menuService.createMenu(123, mockMenu, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void givenNonOwner_whenCreateMenu_throw403Exception() {
        UserDto user = mockUser;
        user.getEmployee().setVendorId(100); // different vendor

        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(user);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> menuService.createMenu(123, mockMenu, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void givenNoDisplayOrder_whenCreateMenu_thenAddAtEnd() {
        Menu menu = mockMenu;
        menu.setDisplayOrder(null);

        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        when(menuRepo.countAllByVendorId(123L)).thenReturn(4);
        when(menuRepo.save(menu)).thenReturn(menu);
        MenuDto menuDto = menuService.createMenu(123, menu, ACCESS_TOKEN);
        assertEquals(4, menuDto.getDisplayOrder());
    }

    @Test
    public void givenValidRequest_whenCreateMenu_thenReturnMenuDto() {
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        when(menuRepo.save(mockMenu)).thenReturn(mockMenu);
        MenuDto menuDto = menuService.createMenu(123, mockMenu, ACCESS_TOKEN);
        assertNotNull(menuDto);
    }

    @Test
    public void givenMoveToTop1_whenUpdateDisplayOrder_thenReturnRenumberedList() {
        List<Menu> mockMenuList = mockMenuList();
        mockMenuList.get(2).setDisplayOrder(0); // move Desserts to the top
        Menu test = mockMenuList.get(2);

        List<Menu> menus = new ArrayList<>();
        menus.add(mockMenuList.get(0)); // Starters w/ display=0
        menus.add(mockMenuList.get(2)); // Desserts w/ display=0
        menus.add(mockMenuList.get(1)); // Mains w/ display=1

        when(menuRepo.findAllByVendorIdOrderByDisplayOrder(123L)).thenReturn(menus);
        List<Menu> updatedMenus = menuService.updateDisplayOrder(test.getId(), test.getVendorId(), test.getDisplayOrder());

        assertEquals(0, updatedMenus.get(0).getDisplayOrder());
        assertEquals(1, updatedMenus.get(1).getDisplayOrder());
        assertEquals(2, updatedMenus.get(2).getDisplayOrder());
        assertEquals("Desserts", updatedMenus.get(0).getName());
        assertEquals("Starters", updatedMenus.get(1).getName());
        assertEquals("Mains", updatedMenus.get(2).getName());
    }

    @Test
    public void givenMoveToTop2_whenUpdateDisplayOrder_thenReturnRenumberedList() {
        List<Menu> mockMenuList = mockMenuList();
        mockMenuList.get(2).setDisplayOrder(0); // move Desserts to the top
        Menu test = mockMenuList.get(2);

        List<Menu> menus = new ArrayList<>();
        menus.add(mockMenuList.get(2)); // Desserts w/ display=0
        menus.add(mockMenuList.get(0)); // Starters w/ display=0
        menus.add(mockMenuList.get(1)); // Mains w/ display=1

        when(menuRepo.findAllByVendorIdOrderByDisplayOrder(123L)).thenReturn(menus);
        List<Menu> updatedMenus = menuService.updateDisplayOrder(test.getId(), test.getVendorId(), test.getDisplayOrder());

        assertEquals(0, updatedMenus.get(0).getDisplayOrder());
        assertEquals(1, updatedMenus.get(1).getDisplayOrder());
        assertEquals(2, updatedMenus.get(2).getDisplayOrder());
        assertEquals("Desserts", updatedMenus.get(0).getName());
        assertEquals("Starters", updatedMenus.get(1).getName());
        assertEquals("Mains", updatedMenus.get(2).getName());
    }

    @Test
    public void givenMoveToBottom_whenUpdateDisplayOrder_thenReturnRenumberedList() {
        List<Menu> mockMenuList = mockMenuList();
        mockMenuList.get(0).setDisplayOrder(2); // move Starters to the bottom
        Menu test = mockMenuList.get(0);

        List<Menu> menus = new ArrayList<>();
        menus.add(mockMenuList.get(1)); // Mains w/ display=1
        menus.add(mockMenuList.get(0)); // Starters w/ display=2
        menus.add(mockMenuList.get(2)); // Desserts w/ display=2

        when(menuRepo.findAllByVendorIdOrderByDisplayOrder(123L)).thenReturn(menus);
        List<Menu> updatedMenus = menuService.updateDisplayOrder(test.getId(), test.getVendorId(), test.getDisplayOrder());

        assertEquals(0, updatedMenus.get(0).getDisplayOrder());
        assertEquals(1, updatedMenus.get(1).getDisplayOrder());
        assertEquals(2, updatedMenus.get(2).getDisplayOrder());
        assertEquals("Mains", updatedMenus.get(0).getName());
        assertEquals("Desserts", updatedMenus.get(1).getName());
        assertEquals("Starters", updatedMenus.get(2).getName());
    }

    @Test
    public void givenMoveToMiddle_whenUpdateDisplayOrder_thenReturnRenumberedList() {
        List<Menu> mockMenuList = mockMenuList();
        mockMenuList.get(0).setDisplayOrder(1); // move Starters to the middle
        Menu test = mockMenuList.get(0);

        List<Menu> menus = new ArrayList<>();
        menus.add(mockMenuList.get(0)); // Starters w/ display=1
        menus.add(mockMenuList.get(1)); // Mains w/ display=1
        menus.add(mockMenuList.get(2)); // Desserts w/ display=2

        when(menuRepo.findAllByVendorIdOrderByDisplayOrder(123L)).thenReturn(menus);
        List<Menu> updatedMenus = menuService.updateDisplayOrder(test.getId(), test.getVendorId(), test.getDisplayOrder());

        assertEquals(0, updatedMenus.get(0).getDisplayOrder());
        assertEquals(1, updatedMenus.get(1).getDisplayOrder());
        assertEquals(2, updatedMenus.get(2).getDisplayOrder());
        assertEquals("Mains", updatedMenus.get(0).getName());
        assertEquals("Starters", updatedMenus.get(1).getName());
        assertEquals("Desserts", updatedMenus.get(2).getName());
    }

    @Test
    public void givenValidRequest_whenUpdateMenu_thenReturnUpdatedMenu() {
        Menu updatedMenu = mockMenu();
        updatedMenu.setName("Changed");
        when(menuRepo.existsByIdAndVendorId(1L, 123L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        when(menuRepo.findAllByVendorIdOrderByDisplayOrder(123L)).thenReturn(List.of(mockMenu));
        when(menuRepo.findById(1L)).thenReturn(Optional.of(mockMenu));
        when(menuRepo.save(any())).thenReturn(updatedMenu);
        when(menuRepo.saveAll(any())).thenReturn(new ArrayList<>());
        MenuDto menu = menuService.updateMenu(123L, 1L, updatedMenu, ACCESS_TOKEN);
        assertEquals(updatedMenu.getName(), menu.getName());
    }

    @Test
    public void givenNotAdmin_whenUpdateMenu_thenThrowException() {
        UserDto user = mockUser;
        user.getEmployee().setAdmin(false);
        when(menuRepo.existsByIdAndVendorId(1L, 123L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(user);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.deleteMenu(123L, 1L, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void givenMenuDoesNotExist_whenUpdateMenu_thenThrowException() {
        when(menuRepo.existsByIdAndVendorId(1L, 123L)).thenReturn(false);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.deleteMenu(123L, 1L, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenValidRequest_whenDeleteMenu_thenCallDelete() {
        when(menuRepo.existsByIdAndVendorId(1L, 123L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        when(menuRepo.findAllByVendorIdOrderByDisplayOrder(123L)).thenReturn(new ArrayList<>());
        when(menuRepo.saveAll(any())).thenReturn(new ArrayList<>());
        menuService.deleteMenu(123L, 1L, ACCESS_TOKEN);
        verify(menuRepo, times(1)).deleteById(1L);
    }

    @Test
    public void givenNotAdmin_whenDeleteMenu_thenThrowException() {
        UserDto user = mockUser;
        user.getEmployee().setAdmin(false);
        when(menuRepo.existsByIdAndVendorId(1L, 123L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(user);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.deleteMenu(123L, 1L, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void givenMenuDoesNotExist_whenDeleteMenu_thenThrowException() {
        when(menuRepo.existsByIdAndVendorId(1L, 123L)).thenReturn(false);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.deleteMenu(123L, 1L, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.NOT_FOUND);
    }

    private Menu mockMenu() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");
        menu.setVendorId(123L);
        menu.setDisplayOrder(0);
        return menu;
    }

    private List<Menu> mockMenuList() {
        List<Menu> menus = new ArrayList<>();

        Menu starters = new Menu();
        starters.setId(1L);
        starters.setVendorId(123L);
        starters.setName("Starters");
        starters.setDisplayOrder(0);

        Menu mains = new Menu();
        mains.setId(2L);
        mains.setVendorId(123L);
        mains.setName("Mains");
        mains.setDisplayOrder(1);

        Menu desserts = new Menu();
        desserts.setId(3L);
        desserts.setVendorId(123L);
        desserts.setName("Desserts");
        desserts.setDisplayOrder(2);

        menus.add(starters);
        menus.add(mains);
        menus.add(desserts);

        return menus;
    }

    private UserDto mockUser() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("test");
        user.setFirstName("Test");
        user.setLastName("Test");

        EmployeeDto emp = new EmployeeDto();
        emp.setEmployeeId(1L);
        emp.setVendorId(123);
        emp.setAdmin(true);
        emp.setAssociate(true);

        user.setEmployee(emp);
        return user;
    }

}
