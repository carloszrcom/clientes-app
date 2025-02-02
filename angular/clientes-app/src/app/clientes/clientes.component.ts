import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import { tap } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { ActivatedRoute } from '@angular/router';
import { ModalService } from './detalle/modal.service';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {

  clientes: Cliente[];
  paginador: any;
  clienteSeleccionado: Cliente;

  constructor(
    private clienteService: ClienteService,
    private activatedRoute: ActivatedRoute,
    private modalService: ModalService
    ) { }

  ngOnInit(): void {
    
    // paramMap se encarga de observar.
    this.activatedRoute.paramMap.subscribe( 
      params => {
        
        // El operador de suma + convierte el string a number.
        let page: number = +params.get('page');

        if (!page) {
          page = 0;
        }

        this.clienteService.getClientes(page).pipe(
          tap(response => {
    
            console.log('ClientesComponent tap3');
            (response.content as Cliente[]).forEach(
              cliente => {
                console.log(cliente.nombre);
              }
            );
          })
        ).subscribe(
          response => {
            this.clientes = response.content as Cliente[];
            this.paginador = response;
          });
      }
    );

    // Actualizar la foto en el listado.

    this.modalService.notificarUpload.subscribe( 
      cliente => {
        this.clientes = this.clientes.map(
          clienteOriginal => {
            if (cliente.id == clienteOriginal.id) {
              clienteOriginal.foto = cliente.foto;
            }
            return clienteOriginal;
          }
        );
      }
    );
  }

  /**
   * Borrar cliente.
   * @param cliente 
   */
  
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

  // Devolver cliente seleccionado.

  public abrirModal(cliente: Cliente) {
    this.clienteSeleccionado = cliente;
    this.modalService.abrirModal();
  }
}
