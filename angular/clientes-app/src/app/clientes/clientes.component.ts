import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import { tap } from 'rxjs/operators';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {

  // Array con los clientes.
  clientes: Cliente[];

  constructor(private clienteService: ClienteService) { }

  ngOnInit(): void {
    // this.clientes = this.clienteService.getClientes();
    // this.clienteService.getClientes.subscribe(
    //   clientes => this.clientes = clientess
    // );

    this.clienteService.getClientes().pipe(
      tap(clientes => {

        this.clientes = clientes; // Antes lo realizábamos en el subscribe.
        console.log('ClientesComponent tap3');
        clientes.forEach(
          cliente => {
            console.log(cliente.nombre);
          }
        );
      })
    ).subscribe();
    //.subscribe(clientes => this.clientes = clientes);
  }

  // Borrar cliente.
  public delete(cliente: Cliente): void {
    const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: 'btn btn-success mx-3',
        cancelButton: 'btn btn-danger'
      },
      buttonsStyling: false
    })
    
    swalWithBootstrapButtons.fire({
      title: '¿Está seguro?',
      text: `¿Seguro que quiere eliminar al cliente ${cliente.nombre} ${cliente. apellidos}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'No, cancelar',
      reverseButtons: false,
      buttonsStyling: false
    }).then((result) => {
      if (result.isConfirmed) {

        this.clienteService.delete(cliente.id).subscribe(
          response => {

            // Mostrar la lista de clientes sin el cliente actual.
            this.clientes = this.clientes.filter(cli => cli !== cliente)

            swalWithBootstrapButtons.fire(
              'Borrado',
              `El cliente <b>${cliente.nombre} ${cliente.apellidos}</b> ha sido eliminado`,
              'success'
            )
          }
        )        
      }
    })
  }

  // Esto es lo mismo que hay en el subscribe pero sin abreviar.
  // function(clientes) {
  //   this.clientes = clientes
  // }


}
