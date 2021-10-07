package com.foodtraffic.menu.service;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.menu.entity.Menu;
import com.foodtraffic.menu.repository.MenuItemRepository;
import com.foodtraffic.menu.repository.MenuRepository;
import com.foodtraffic.model.dto.EmployeeDto;
import com.foodtraffic.model.dto.MenuDto;
import com.foodtraffic.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
public class MenuTest {

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

    private Menu mockMenu;

    private UserDto mockUser;

    private static final String ACCESS_TOKEN = "abcdefg123";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
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

    private Menu mockMenu() {
        Menu menu = new Menu();
        menu.setName("Test Menu");
        menu.setVendorId(123L);
        menu.setDisplayOrder(0);
        return menu;
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
