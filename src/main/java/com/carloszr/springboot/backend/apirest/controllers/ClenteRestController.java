package com.carloszr.springboot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.carloszr.springboot.backend.apirest.models.entity.Cliente;
import com.carloszr.springboot.backend.apirest.models.services.IClienteService;

// Puerto de angular 4200. Así damos acceso a este dominio para que pueda enviar y recibir datos.
@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClenteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	// Listar los clientes.
	
	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	// Encontrar cliente por id.
	// Si no se encuentra un cliente devuelve un error, por eso se devuelve ResponseEntity.
	// <?>, que puedde devolver cualquier tipo de objeto.
	// @ResponseStatus(HttpStatus.OK) // Si se realiza correctamente, OK(200) es lo que se devuelve.
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		// return cuando siempre se devolvía un cliente.
		// No tenía en cuenta que el cliente no pudiera existir.
//		return clienteService.findById(id);
		
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		
		// Intenta realizar la consulta del cliente.
		try {
			cliente = clienteService.findById(id);			
		} catch (DataAccessException dataAccessException) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos.");
			response.put("error", dataAccessException.getMessage().concat(":").concat(dataAccessException.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
				
		// Comprueba si existe el cliente.
		if (cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos.")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		} else {
			
		}
		
		System.out.println("DEBUG: nombre del cliente --> " + cliente.getNombre());
		
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	
	// Crear un cliente.
	
	// @Valid: valida, sin esta anotación no funcionan las validaciones en las entidades.
	// BindingResult: objeto que contiene todos los mensajes de error de las validaciones.
	@PostMapping(path = "/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente newCliente = null;
		Map<String, Object> response = new HashMap<>();
		
		// Comprobar si hay errores.
		
		if (result.hasErrors()) {
			
			// Forma de hacerlo con API Stream de Java. Más limpio. Programación funcional.
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map( err -> "El campo: '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			// Forma de hacerlo con bucle for. Anterior a JDK 8.
			/*List<String> errors = new ArrayList<>();
			
			for (FieldError err: result.getFieldErrors()) {
				
				errors.add("El campo: '" + err.getField() + "'" + err.getDefaultMessage());
				
			} */
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		
		// Intentar guardar el cliente.
		
		try {
			newCliente = clienteService.save(cliente);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido creado con éxito.");
		response.put("cliente", newCliente);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);  
	}
	
	// Actualizar un cliente.
	// Importante respetar el orden de los parámetros.
	
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdated = null;
		
		Map<String, Object> response = new HashMap<>();
		
		// Comprobar si hay errores.
		
		if (result.hasErrors()) {
			
			// Forma de hacerlo con API Stream de Java. Más limpio. Programación funcional.
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map( err -> "El campo: '" + err.getField() + "'" + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// Comprueba si existe el cliente.
		if (clienteActual == null) {
			response.put("mensaje", "Error, no se pudo editar, cliente ID: ".concat(id.toString().concat(" no existe en la base de datos.")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		} else {
			
		}
		
		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellidos(cliente.getApellidos());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			
			clienteUpdated = clienteService.save(clienteActual);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el cliente en la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido actualizado con éxito.");
		response.put("cliente", cliente);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	// Borrar un cliente.
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente de la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Cliente eliminado con éxito.");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			
	}
	
	
	
	
	
	
	
	
	
	
	
	// Crear un cliente devolviendo objeto de tipo Cliente.
	
//		@PostMapping(path = "/clientes")
//		@ResponseStatus(HttpStatus.CREATED)
//		public Cliente create(@RequestBody Cliente cliente) {
//			
//			// La fecha de creación la vamos ha poner mejor en la clase entity con prepersist,
//			// donde antes que se haga un save nos va a incluir la fecha.
//			// cliente.setCreateAt(new Date());
//			return clienteService.save(cliente);
//		}
	
	// Actualizar un cliente devolviendo un cliente.
//	
//		@PutMapping("/clientes/{id}")
//		@ResponseStatus(HttpStatus.CREATED)
//		public Cliente update(@RequestBody Cliente cliente, @PathVariable Long id) {
//			
//			Cliente clienteActual = clienteService.findById(id);
//			
//			clienteActual.setNombre(cliente.getNombre());
//			clienteActual.setApellidos(cliente.getApellidos());
//			clienteActual.setEmail(cliente.getEmail());
//			
//			return clienteService.save(clienteActual);
//		}
//		
//		// Borrar un cliente.
//		
//		@DeleteMapping("/clientes/{id}")
//		@ResponseStatus(HttpStatus.NO_CONTENT)
//		public void delete(@PathVariable Long id) {
//			clienteService.delete(id);
//		}
	
//	// Borrar un cliente.
//	
//		@DeleteMapping("/clientes/{id}")
//		@ResponseStatus(HttpStatus.NO_CONTENT)
//		public void delete(@PathVariable Long id) {
//			clienteService.delete(id);
//		}
}
