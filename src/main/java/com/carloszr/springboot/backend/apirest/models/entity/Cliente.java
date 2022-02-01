package com.carloszr.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "no puede estar vacío.")
	@Size(min = 4, max = 12, message = "el tamaño tiene que estar entre 4 y 12.")
	@Column(name = "nombre", nullable = false)
	private String nombre;
	
	@NotEmpty(message = "no puede estar vacío.")
	@Column(name = "apellidos")
	private String apellidos;
	
	
	/**
	 * En producción poner unique true para no tener emails duplicados.
	 */
	
	@NotEmpty(message = "no puede estar vacío.")
	@Email(message = "no es una dirección de correo correcta.")
	@Column(name = "email", nullable = false, unique = false, length = 200)
	private String email;
	
	@NotNull(message = "no puede ser null.")
	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	/**
	 * Evento del ciclo de vida de las clases entity.
	 */
	
	// Ahora se maneja la fecha con un DatePicker en el formulario.
	
//	@PrePersist
//	public void prePersist() {
//		createAt = new Date();
//	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellidos() {
		return apellidos;
	}
	
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getCreateAt() {
		return createAt;
	}
	
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
		
}
