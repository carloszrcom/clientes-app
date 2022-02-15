package com.carloszr.springboot.backend.apirest.models.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Con la anotación Service definimos la clase como un objeto de Spring.
 * De esta forma va a quedar guardada en el contenedor de Spring y después lo podemos 
 * inyectar y utilizar en el controlador.
 * Con Service indicamos que es del tipo component, es decir, un Bean de Spring.
 * @author carloszr
 *
 */

@Service
public class UploadFileServiceImpl implements IUploadFileService {
	
	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);
	private final static String DIRECTORIO_UPLOADS = "uploads";
	private final static String FOTO_NO_USUARIO = "no-usuario.png";

	@Override
	public Resource cargarFoto(String nombreFoto) throws MalformedURLException {
		
		Path rutaArchivo = getPath(nombreFoto); 		
		log.info(rutaArchivo.toString());
		
		// A partir de la ruta creamos el recurso.
		
		Resource recurso = new UrlResource(rutaArchivo.toUri());;
		
		// Lanzar excepción si la imagen no existe o no se pudo leer.
		
		if (!recurso.exists() && !recurso.isReadable()) {
			
			rutaArchivo = Paths.get("src/main/resources/static/images").resolve(FOTO_NO_USUARIO).toAbsolutePath();
			recurso = new UrlResource(rutaArchivo.toUri());
			log.error("Error, no se pudo cargar la imagen " + nombreFoto);
		}
		
		return recurso;
	}
	
	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		
		
		// Obtener el archivo y la ruta.

		// Obtenemos el nombre del archivo.
		String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");

		// Seleccionar una ruta en nuestro equipo, en cualquier parte de nuestro
		// servidor.
		Path rutaArchivo = getPath(nombreArchivo);
		log.info(rutaArchivo.toString());

		// Copiar el archivo a la ruta escogida.

		Files.copy(archivo.getInputStream(), rutaArchivo);
		
		return nombreArchivo;
	}

	@Override
	public boolean eliminar(String nombreFoto) {
		
		if (nombreFoto != null && nombreFoto.length() > 0) {

			Path rutaFotoAnterior = Paths.get(DIRECTORIO_UPLOADS).resolve(nombreFoto).toAbsolutePath();
			File archivoFotoAnterior = rutaFotoAnterior.toFile();

			// Si todo sale bien devuelve true.
			
			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
				
				return true;
			}
		}
		
		// Devuelve false si algo sale mal.
		
		return false;
	}

	@Override
	public Path getPath(String nombreFoto) {
		
		Path path = null;
		
		try {
			path = Paths.get(DIRECTORIO_UPLOADS).resolve(nombreFoto).toAbsolutePath();
		} catch (InvalidPathException e) {
			log.info("ERROR al crear el path: " + e.getMessage());
		}
		
		return path;
	}
}
