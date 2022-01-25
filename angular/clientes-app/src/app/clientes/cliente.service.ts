// import { DatePipe, formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { Cliente } from './cliente';
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

  // Crear cliente.
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
  
  // Obtener un cliente por su id manejando errores.

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

  // Actualizar cliente.

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

  // Borrar cliente.
  
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

}
