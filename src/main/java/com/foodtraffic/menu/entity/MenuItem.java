package com.foodtraffic.menu.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "MENU_ITEM")
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUITEMID")
	private Long id;
	
	@Column(name = "MENUID")
	private Long menuId;

	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 100, message = "Name cannot exceed 100 characters")
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;

	@NotNull(message = "Price is required")
	@Min(value = 0, message = "Price cannot be negative")
	@Column(name = "PRICE")
	private Double price;

	@Min(value = 0, message = "Calories cannot be negative")
	@Column(name = "CALORIES")
	private Integer calories;

	@Size(max = 300, message = "Remarks cannot exceed 300 characters")
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "IS_VEGAN")
	@JsonProperty(value = "vegan")
	private Boolean isVegan;
	
	@Column(name = "IS_VEGETARIAN")
	@JsonProperty(value = "vegetarian")
	private Boolean isVegetarian;
	
	@Column(name = "IS_GLUTEN_FREE")
	@JsonProperty(value = "glutenFree")
	private Boolean isGlutenFree;
	
	@Column(name = "IS_DAIRY_FREE")
	@JsonProperty(value = "dairyFree")
	private Boolean isDairyFree;
	
	@Column(name = "CONTAINS_NUTS")
	private Boolean containsNuts;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		MenuItem menuItem = (MenuItem) o;
		return Objects.equals(id, menuItem.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
