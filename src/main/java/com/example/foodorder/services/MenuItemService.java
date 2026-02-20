package com.example.foodorder.services;

import com.example.foodorder.entities.MenuItem;
import java.util.List;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();

    List<MenuItem> getMenuItemsByRestaurant(Long restaurantId);

    MenuItem getMenuItemById(Long id);

    MenuItem createMenuItem(MenuItem menuItem);

    MenuItem updateMenuItem(Long id, MenuItem menuItemDetails);

    void deleteMenuItem(Long id);
}
