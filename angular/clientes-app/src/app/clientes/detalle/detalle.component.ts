import { HttpEventType, HttpParams } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Cliente } from '../cliente';
import { ClienteService } from '../cliente.service';
import Swal from 'sweetalert2';
import { ModalService } from './modal.service';

@Component({
  selector: 'detalle-cliente',
  templateUrl: './detalle.component.html',
  styleUrls: ['./detalle.component.css']
})
export class DetalleComponent implements OnInit {

  @Input() cliente: Cliente;
  
  titulo: string = "Detalle del cliente";
  public fotoSeleccionada: File;
  public progreso: number = 0;

  // Inyectamos clienteService vía constructor.
  // Inyectamos también ActivatedRoute para poder suscribir cuando cambia el parámetro del id.

  constructor(
    private clienteService: ClienteService,
    public modalService: ModalService
    // private activatedRoute: ActivatedRoute // Ya no se utiliza por implementar modal.
    ) { }

  ngOnInit(): void {

// Al implementar modal (pasamos el cliente por @Input) ya no es necesario pasar el cliente a la ruta. Por eso se comenta lo de abajo.

    // // Suscribir cuando cambia el parámetro del id para poder obtener el detalle cliente.

    // this.activatedRoute.paramMap.subscribe(
    //   params => {
    //     let id: number = +params.get('id');

    //     // Si el id existe obtenemos el cliente mediante la clase Service.

    //     if (id) {
    //       this.clienteService.getCliente(id).subscribe(
    //         cliente => {
    //           this.cliente = cliente;
    //         }
    //       );
    //     }
    //   }
    // );
  }

  /**
   * Seleccionar foto.
   * @param event
   */

  public seleccionarFoto(event) {
    this.fotoSeleccionada = event.target.files[0];
    console.log(this.fotoSeleccionada);

    // Reiniciar la barra de progreso para la subida de la foto.
    this.progreso = 0;

    // Comprobar que la foto es un archivo de imagen.
    if (this.fotoSeleccionada.type.indexOf('image') < 0) {
      Swal.fire('Error', 'Debe seleccionar un archivo de imagen válido', 'error');
      this.fotoSeleccionada = null;
    }
  }

  /**
   * Subir foto, botón Aceptar.
   */

  public subirFoto() {

    if (!this.fotoSeleccionada) {
      Swal.fire('Error al subir foto', 'Debe seleccionar una foto', 'error');
    } else {
      this.clienteService.subirFoto(this.fotoSeleccionada, this.cliente.id).subscribe(

        // Implementar la subida con barra de progreso.

        event => {

          if (event.type === HttpEventType.UploadProgress) {
            this.progreso = Math.round((event.loaded / event.total) * 100);            
          } else if (event.type === HttpEventType.Response) {
            let response: any = event.body;
            this.cliente = response.cliente as Cliente;
            Swal.fire('La foto se ha subido completamente', response.mensaje, 'success');
          }
        }


        // Antes de implementar la barra de carga se devolvía un observable de Cliente.

        // cliente => {
        //   this.cliente = cliente;
        //   Swal.fire('La foto se ha subido completamente', `La foto se ha subido con éxito: ${this.cliente.foto}`, 'success');
        // }
      );
    }
  }

  // Cerrar la ventana modal de detalle de cliente.

  public cerrarModalDetalleComponent() {
    
    this.modalService.cerrarModal();

    this.fotoSeleccionada = null;
    this.progreso = 0;
  }
}
