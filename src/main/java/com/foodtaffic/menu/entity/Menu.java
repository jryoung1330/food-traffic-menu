package com.foodtaffic.menu.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.Data;

@Entity
@Table(name = "MENU")
@Data
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUID")
	@Min(0)
	private Long id;

	@Column(name = "FOODTRUCKID")
	private Long foodTruckId;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany
	@JoinColumn(name = "MENUID", updatable=false)
	private Set<MenuItem> menuItems;

}
