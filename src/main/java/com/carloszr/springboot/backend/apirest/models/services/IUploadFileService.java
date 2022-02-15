package com.carloszr.springboot.backend.apirest.models.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	/**
	 * Carga la foto.
	 * @param nombreFoto
	 * @return
	 * @throws MalformedURLException
	 */
	
	public Resource cargarFoto(String nombreFoto) throws MalformedURLException;	

	/**
	 * Devuelve el nombre del archivo generado. 
	 */

	public String copiar(MultipartFile archivo) throws IOException;
	
	/**
	 * 
	 * @param nombreFoto
	 * @return
	 */
	
	public boolean eliminar(String nombreFoto);
	
	
	public Path getPath(String nombreFoto);
}
