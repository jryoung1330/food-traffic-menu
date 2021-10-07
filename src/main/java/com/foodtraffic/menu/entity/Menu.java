package com.foodtraffic.menu.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
	@ToString.Exclude
	private List<MenuItem> menuItems;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Menu menu = (Menu) o;
		return Objects.equals(id, menu.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}

