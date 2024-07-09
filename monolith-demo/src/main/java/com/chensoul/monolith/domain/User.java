package com.chensoul.monolith.domain;

import static com.chensoul.monolith.domain.User.TABLE_NAME;
import com.chensoul.sharedlib.jpa.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TABLE_NAME)
public class User extends AuditedEntity {
	public static final String TABLE_NAME = "users";
	private static final long serialVersionUID = -3552577854495026179L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@JsonIgnore
	@Column(name = "`key`", unique = true, nullable = false)
	private String key;

	@Column(nullable = false)
	private Boolean isActive;
}
