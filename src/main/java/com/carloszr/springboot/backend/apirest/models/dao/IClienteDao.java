package com.carloszr.springboot.backend.apirest.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import com.carloszr.springboot.backend.apirest.models.entity.Cliente;
import com.carloszr.springboot.backend.apirest.models.entity.Region;

// No extiende de CrudRepository para poder implementar paginación.

public interface IClienteDao extends JpaRepository<Cliente, Long> {

	// Devuelve todas las regiones para que podamos seleccionar la región
	// del cliente en el formulario.
	// Tenemos que mapear el método a una consulta JPQL(JPA Query), que es la forma de 
	// utilizar repositories en Spring Data JPA.
	// Recordar que en la Query estamos trabajando con OBJETOS y no con tablas.
	// "from Region", una consulta abreviada para que retorne todas las regiones.
	
	@Query("from Region")
	public List<Region> findAllRegiones();
}
