// import { DatePipe, formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { Cliente } from './cliente';
import { HttpRequest, HttpEvent } from '@angular/common/http';

// import { CLIENTES } from './clientes.json';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private urlEndPoint: string = "http://localhost:8080/api/clientes";
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router) {}

  public getClientes(page: number): Observable<any> {
    
    return this.http.get(this.urlEndPoint + '/page/' + page).pipe(
      
      // El operazor tap realiza algo, pero no transforma.
      tap(
        (response: any) => {
          console.log('DEBUG: ClienteService tap 1');
          
          // Casting.
          (response.content as Cliente[]).forEach(cliente => {
          console.log(cliente.nombre);
          });
        }
      ),
      map((response: any) => {
        
        (response.content as Cliente[]).map(
          cliente => {
            cliente.nombre = cliente.nombre.toUpperCase();
            return cliente;
          }
        );
        return response;
      }),
      tap(
        response => {
          console.log('DEBUG: ClienteService tap 2');
          (response.content as Cliente[]).forEach(cliente => {
            console.log(cliente.nombre);
          });
        }
      )
    );
  }

  /**
   * Crear Cliente.
   * @param cliente 
   * @returns 
   */

  public create(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.urlEndPoint, cliente, {headers: this.httpHeaders}).pipe(
      // Convertir a tipo Cliente.
      map((response: any) => response.cliente as Cliente),
      // Capturamos un error.
      catchError(
        e => {

          // Si el servidor devuelve status 400.
          if (e.status == 400) {
            // Pasamos los errores a la plantilla.
            return throwError(() => e);
          }

          console.error(e.error.mensaje);
          Swal.fire(e.error.mensaje, e.error.error, 'error');
          return throwError(() => e);
        }
      )
    );
  }
  
  /**
   * Obtener un cliente por su id manejando errores.
   * @param id
   * @returns 
   */

  public getCliente(id): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.urlEndPoint}/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/clientes']);
        console.error(e.error.mensaje);
        Swal.fire('Error al editar', e.error.mensaje, 'error');
        
        return throwError(() => e);
        })
      );
  }

  /**
   * Actualizar cliente.
   * @param cliente 
   * @returns 
   */

  public update(cliente: Cliente): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint}/${cliente.id}`, cliente, {headers: this.httpHeaders}).pipe(
      catchError(
        e => {

          // Si el servidor devuelve status 400.
          if (e.status == 400) {
            // Pasamos los errores a la plantilla.
            return throwError(() => e);
          }

          console.error(e.error.mensaje);
          Swal.fire(e.error.mensaje, e.error.error, 'error');
          return throwError(() => e);
        }
      )
    );
  }

  /**
   * Borrar cliente.
   * @param id 
   * @returns 
   */
  
  public delete(id): Observable<Cliente> {
    return this.http.delete<Cliente>(`${this.urlEndPoint}/${id}`, {headers: this.httpHeaders}).pipe(
      catchError(
        e => {
          console.error(e.error.mensaje);
          Swal.fire(e.error.mensaje, e.error.error, 'error');
          return throwError(() => e);
        }
      )
    );
  }

  
  /**
   * Subir foto.
   * @param archivo 
   * @param id 
   * @returns 
   */

  // Al implementar la barra de progreso ya no retorna Observable<Cliente>.
  public subirFoto(archivo: File, id): Observable<HttpEvent<{}>> {

    let formData = new FormData();
    formData.append("archivo", archivo);
    formData.append("id", id);

    // Implementar barra de progreso.

    const req = new HttpRequest('POST', `${this.urlEndPoint}/upload/`, formData, {
      reportProgress: true
    });

    return this.http.request(req);

    // Sin barra de progreso.

    // return this.http.post(`${this.urlEndPoint}/upload`, formData).pipe(
    //   map(
    //     // (response) => console.info(response)
    //     (response: any) => response.cliente as Cliente
    //   ),
    //   catchError(
    //     e => {
    //       console.error(e.error.mensaje);
    //       Swal.fire(e.error.mensaje, e.error.error, 'error');
    //       return throwError(() => e);
    //     }
    //   )
    // );
  }

}
