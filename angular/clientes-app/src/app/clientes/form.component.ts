import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import swal from 'sweetalert2';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';


@Component({
  selector: 'app-form',
  templateUrl: './form.component.html'
})
export class FormComponent implements OnInit {

  public titulo: string;

  public cliente: Cliente = new Cliente();

  public errores: string[];

  constructor(
    private clienteService: ClienteService,
    private router: Router,
    private activatedRoute: ActivatedRoute
    ) { }

  ngOnInit(): void {
    this.cargarCliente();
  }

  // Cargar cliente.
  private cargarCliente(): void {
    this.activatedRoute.params.subscribe(
      params => {
        let id = params['id'];
        if (id) {
          console.log('DEBUG: el cliente es NOMBRE: ' + params['nombre']);
          // Registrar al observador que asigna el cliente de la consulta al atributo cliente.
          this.clienteService.getCliente(id).subscribe(
            cliente => this.cliente = cliente
          );
        }
      }
    );
  }

  // Crear cliente.
  public create(): void {
    
    this.clienteService.create(this.cliente).subscribe(
      cliente => {
        this.router.navigate(['/clientes']);
        // cliente es el objeto que se manda desde el backend.
        swal.fire('Nuevo cliente', `El cliente <b>${cliente.nombre} </b> ha sido creado con éxito.`, 'success');
      },
      err => {
        console.log('DEBUG: objeto err --> ' + err);
        this.errores = err.error.errors as string[];
        console.error('Código de error desde el backend: ' + err.status);
        console.error(err.error.errors);
      }
    );
  }

  // Actualizar un cliente.
  public update(): void {
    
    this.clienteService.update(this.cliente).subscribe(
      json => {
        this.router.navigate(['/clientes']);
        swal.fire('Cliente actualizado', `${json.mensaje}<br>Cliente: <b>${json.cliente.nombre}</b>`, 'success');
      },
      err => {
        console.log('DEBUG: objeto err --> ' + err);
        this.errores = err.error.errors as string[];
        console.error('Código de error desde el backend: ' + err.status);
        console.error(err.error.errors);
      }
    );
  }  
}
