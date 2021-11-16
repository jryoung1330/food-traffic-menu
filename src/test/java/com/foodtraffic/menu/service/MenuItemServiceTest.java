package com.foodtraffic.menu.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.menu.entity.MenuItem;
import com.foodtraffic.menu.repository.MenuItemRepository;
import com.foodtraffic.menu.repository.MenuRepository;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.MenuItemDto;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuItemServiceTest {

    @Mock
    MenuRepository menuRepo;

    @Mock
    MenuItemRepository menuItemRepo;

    @Mock
    UserClient userClient;

    @Spy
    ModelMapper modelMapper;

    @InjectMocks
    MenuServiceImpl menuService;

    private MenuItem mockMenuItem;

    private UserDto mockUser;

    private static final String ACCESS_TOKEN = "test";

    @BeforeEach
    public void setup() {
        mockMenuItem = mockMenuItem();
        mockUser = mockUser();
    }

    @Test
    public void givenValidRequest_whenCreateMenuItem_thenReturnMenuItem() {
        when(menuRepo.existsById(1L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        when(menuItemRepo.save(any())).thenReturn(mockMenuItem);
        MenuItemDto item = menuService.createMenuItem(123L, 1L, mockMenuItem, ACCESS_TOKEN);
        assertNotNull(item);
    }

    @Test
    public void givenMenuDoesNotExist_whenCreateMenuItem_thenThrowException() {
        when(menuRepo.existsById(1L)).thenReturn(false);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.createMenuItem(123L, 1L, mockMenuItem, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenUserNotAdmin_whenCreateMenuItem_thenThrowException() {
        mockUser.getEmployee().setAdmin(false);
        when(menuRepo.existsById(1L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.createMenuItem(123L, 1L, mockMenuItem, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void givenValidRequest_whenUpdateMenuItem_thenReturnMenuItem() {
        when(menuItemRepo.existsByIdAndMenuId(1L, 1L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        when(menuItemRepo.findById(1L)).thenReturn(Optional.of(mockMenuItem));
        when(menuItemRepo.save(any())).thenReturn(mockMenuItem);
        MenuItemDto item = menuService.updateMenuItem(123L, 1L, 1L, mockMenuItem, ACCESS_TOKEN);
        assertNotNull(item);
    }

    @Test
    public void givenMenuItemDoesNotExist_whenUpdateMenuItem_throwException() {
        when(menuItemRepo.existsByIdAndMenuId(1L, 1L)).thenReturn(false);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.updateMenuItem(123L, 1L, 1L, mockMenuItem, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenUserNotAdmin_whenUpdateMenuItem_throwException() {
        mockUser.getEmployee().setAdmin(false);
        when(menuItemRepo.existsByIdAndMenuId(1L, 1L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.updateMenuItem(123L, 1L, 1L, mockMenuItem, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void givenValidRequest_whenDeleteMenuItem_thenCallDelete() {
        when(menuItemRepo.existsByIdAndMenuId(1L, 1L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        menuService.deleteMenuItem(123L, 1L, 1L, ACCESS_TOKEN);
        verify(menuItemRepo, times(1)).deleteById(1L);
    }

    @Test
    public void givenMenuItemDoesNotExist_whenDeleteMenuItem_throwException() {
        when(menuItemRepo.existsByIdAndMenuId(1L, 1L)).thenReturn(false);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.deleteMenuItem(123L, 1L, 1L, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenUserNotAdmin_whenDeleteMenuItem_throwException() {
        mockUser.getEmployee().setAdmin(false);
        when(menuItemRepo.existsByIdAndMenuId(1L, 1L)).thenReturn(true);
        when(userClient.checkAccessHeader(ACCESS_TOKEN)).thenReturn(mockUser);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class,
                () -> menuService.deleteMenuItem(123L, 1L, 1L, ACCESS_TOKEN));
        assertEquals(rse.getStatus(), HttpStatus.FORBIDDEN);
    }

    private MenuItem mockMenuItem() {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setMenuId(1L);
        menuItem.setName("Pizza");
        menuItem.setCalories(1000);
        menuItem.setIsVegetarian(false);
        return menuItem;
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
