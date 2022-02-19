package com.carloszr.springboot.backend.apirest.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.carloszr.springboot.backend.apirest.models.entity.Cliente;
import com.carloszr.springboot.backend.apirest.models.entity.Region;

public interface IClienteService {
	
	// Listar todos los clientes.

	public List<Cliente> findAll();
	
	// Listar con Page.
	
	public Page<Cliente> findAll(Pageable pageable);
	
	// Encontrar y devolver un cliente.
	
	public Cliente findById(Long id);
	
	// Guardar un cliente.
	
	public Cliente save(Cliente cliente);
	
	// Borrar un cliente.
	
	public void delete(Long id);
	
	// Listar las Regiones.
	
	public List<Region> findAllRegiones();
}
