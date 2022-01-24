package com.carloszr.springboot.backend.apirest.models.services;

import java.util.List;

import com.carloszr.springboot.backend.apirest.models.entity.Cliente;

public interface IClienteService {
	
	// Listar todos los clientes.

	public List<Cliente> findAll();
	
	// Encontrar y devolver un cliente.
	
	public Cliente findById(Long id);
	
	// Guardar un cliente.
	
	public Cliente save(Cliente cliente);
	
	// Borrar un cliente.
	
	public void delete(Long id);
}
