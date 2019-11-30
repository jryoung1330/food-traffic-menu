package com.foodtaffic.menu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.Data;

@Entity
@Table(name = "MENU_ITEM")
@Data
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUITEMID")
	@Min(0)
	private Long id;
	
	@Column(name = "MENUID")
	private Long menuId;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PRICE")
	private Double price;
	
	@Column(name = "CALORIES")
	private Integer calories;
	
	@Column(name = "INGREDIENTS")
	private String ingredients;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "IS_VEGAN")
	private Boolean isVegan;
	
	@Column(name = "IS_VEGETARIAN")
	private Boolean isVegetarian;
	
	@Column(name = "IS_GLUTEN_FREE")
	private Boolean isGlutenFree;
	
	@Column(name = "IS_DAIRY_FREE")
	private Boolean isDairyFree;
	
	@Column(name = "CONTAINS_NUTS")
	private Boolean containsNuts;
}
