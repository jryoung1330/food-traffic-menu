package com.foodtraffic.menu.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "MENU")
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MENUID")
	private Long id;

	@Column(name = "VENDORID")
	private Long vendorId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;

	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "MENUID", updatable=false)
	private List<MenuItem> menuItems;

}

