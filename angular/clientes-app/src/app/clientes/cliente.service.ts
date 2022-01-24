import { DatePipe, formatDate } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { Cliente } from './cliente';
import { CLIENTES } from './clientes.json';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private urlEndPoint: string = "http://localhost:8080/api/clientes";

  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router) {}

  // Método asíncrono.
  // of convierte CLIENTES en un stream reactivo.
  // Otra forma de hacerlo usando el operador map.

  public getClientes(): Observable<Cliente[]> {
    // return of(CLIENTES);
    return this.http.get(this.urlEndPoint).pipe(
      
      // El operazor tap realiza algo, pero no transforma.
      tap(
        response => {
          console.log('DEBUG: ClienteService tap 1');
          let clientes = response as Cliente[];
          clientes.forEach(cliente => {
            console.log(cliente.nombre);
          });
        }
      ),
      map((response) => {
        
        let clientes = response as Cliente[];
        return clientes.map(
          cliente => {
            cliente.nombre = cliente.nombre.toUpperCase();
            
            // Una forma de cambiar el formato de la fecha.
            // cliente.createAt = formatDate(cliente.createAt, 'dd-MM-yyyy', 'en-US');

            // Añadir español al locale. Por defecto es 'en-US' y no hay que hacer import.
            // Mejor lo añadimos en el appmodule. Así está importado para toda la app.
            // registerLocaleData(localeES, 'es');

            // Otra forma de cambiar el formato de la fecha.
            // let datePipe = new DatePipe('en-US');
            let datePipe = new DatePipe('es');

            // Día de la semana abreviado:
            // cliente.createAt = datePipe.transform(cliente.createAt, 'EEE dd/MM/yyyy');
            // Nombre del mes abreviado:
            // cliente.createAt = datePipe.transform(cliente.createAt, 'EEEE dd/MMM/yyyy');
            // Nombre del mes completo:
            // cliente.createAt = datePipe.transform(cliente.createAt, 'EEEE dd/MMMM/yyyy');
            // Fecha completa:
            // cliente.createAt = datePipe.transform(cliente.createAt, 'fullDate');
            
            // Comentamos esto para realizar la conversión de la fecha en la plantilla html.
            // cliente.createAt = datePipe.transform(cliente.createAt, 'EEEE dd, MMMM yyyy');

            return cliente;
          }
        );
      }),
      tap(
        response => {
          console.log('DEBUG: ClienteService tap 2');
          response.forEach(cliente => {
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

  // Crear cliente. Creamos un Observable de tipo any y así no tenemos que hacer el map para convertir a un observable de tipo Cliente.
  // public create(cliente: Cliente): Observable<any> {
  //   return this.http.post<any>(this.urlEndPoint, cliente, {headers: this.httpHeaders}).pipe(
  //     catchError(
  //       e => {
  //         console.error(e.error.mensaje);
  //         Swal.fire(e.error.mensaje, e.error.error, 'error');
  //         return throwError(() => e);
  //       }
  //     )
  //   );
  // }


  // Sin Observable (cuando era un método síncrono).
  // getClientes(): Cliente[] {
  //   return CLIENTES;
  // }

  // Método asíncrono.
  // of convierte CLIENTES en un stream reactivo.
  // getClientes(): Observable<Cliente[]> {
  //   // return of(CLIENTES);
  //   return this.http.get<Cliente[]>(this.urlEndPoint);
  // }

  // Obtener un cliente por su id.
  // public getCliente(id: number): Observable<Cliente> {
  //   console.log('Ejecutando desde cliente.service el getCliente')
  //   return this.http.get<Cliente>(`${this.urlEndPoint}/${id}`);
  // }
}
