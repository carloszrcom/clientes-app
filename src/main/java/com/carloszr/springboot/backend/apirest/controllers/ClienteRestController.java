package com.carloszr.springboot.backend.apirest.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carloszr.springboot.backend.apirest.models.entity.Cliente;
import com.carloszr.springboot.backend.apirest.models.services.IClienteService;
import com.carloszr.springboot.backend.apirest.models.services.IUploadFileService;

// Puerto de angular 4200. Así damos acceso a este dominio para que pueda enviar y recibir datos.
@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	private final Logger log = LoggerFactory.getLogger(ClienteRestController.class);
	
	// Listar los clientes.
	
	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 4);
		return clienteService.findAll(pageable);
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
	
	/**
	 * Borrar un cliente.
	 * @param id
	 * @return
	 */
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			// Borrar primero la foto si tiene.
			
			// Si el cliente ya tiene una foto, borrarla.
			
			Cliente cliente = clienteService.findById(id);
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadFileService.eliminar(nombreFotoAnterior);
			
			// Borrar el cliente.
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente de la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Cliente eliminado con éxito.");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * Para subir una imagen del cliente.
	 * @param archivo
	 * @param id
	 * @return
	 */
	
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		Cliente cliente = clienteService.findById(id);
		
		// Si hay imagen la copiamos.
		if(!archivo.isEmpty()) {
			
			
			
			// Copiar el archivo a la ruta escogida.
			
			String nombreArchivo = null;
			
			try {
				nombreArchivo = uploadFileService.copiar(archivo);
			} catch (IOException e) {
				
				// Devolver error.
				
				response.put("mensaje", "Error al subir la imagen del cliente");
				response.put("error", e.getMessage().concat(":").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			// Si el cliente ya tiene una foto, borrarla.
			
			String nombreFotoAnterior = cliente.getFoto();			
			
			// Eliminar la foto anterior.
			
			uploadFileService.eliminar(nombreFotoAnterior);
			
			// Establecer la foto al cliente.
			cliente.setFoto(nombreArchivo);
			
			// Actualizar el cliente.
			clienteService.save(cliente);
			
			// Pasamos el objeto cliente y un mensaje a la respuesta.
			
			response.put("cliente", cliente);
			response.put("mensaje", "Ha subido correctamente la imagen: " + nombreArchivo);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	// Método handler para mostrar foto.
	// La ruta de la foto contiene el nombre del archivo, un punto y la extensión del archivo (expresión regular).
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
		
		// A partir de la ruta creamos el recurso.
		
		Resource recurso = null;
		
		try {
			recurso = uploadFileService.cargarFoto(nombreFoto);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Pasar las cabeceras de la respuesta para que este recurso se pueda descargar.
		// Forzar que el archivo se descargue.
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}	
}
