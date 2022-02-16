package com.carloszr.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
//import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
	
	@Column(name = "foto")
	private String foto;
	
	// Un cliente puede tener una región, pero una región puede tener muchos clientes.
	// Entonces, relación ManyToOne.
	
	// LAZY, carga perezosa, cuando se llame al get entonces se realiza la carga.
	// Al trabajar con API REST, el listado de clientes
	// se va a transformar en un JSON de forma automática,
	// En el caso de Región se genera otro objeto, otro JSON, anidado dentro del
	// JSON principal de Clientes. Cuando se invoca getRegion va a realizar la consulta a la bd,
	// para acceder a estos objetos relacionados. Debajo, de forma transparente va a generar un objeto
	// Region  que es un proxy, es como un puente para poder acceder a estos datos.
	// Si omitimos la anotación JoinColumn, va a generar de todas formas un campo, pero mejor no omitirlo.
	// El proxy genera atributos adicionales. Mejor ignorarlos para que no de error.
	
	@ManyToOne
	@JoinColumn(name = "region_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Region region;
	
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
	
	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
