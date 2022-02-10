import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Cliente } from '../cliente';
import { ClienteService } from '../cliente.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'detalle-cliente',
  templateUrl: './detalle.component.html',
  styleUrls: ['./detalle.component.css']
})
export class DetalleComponent implements OnInit {

  cliente: Cliente;
  titulo: string = "Detalle del cliente";
  private fotoSeleccionada: File;

  // Inyectamos clienteService vía constructor.
  // Inyectamos también ActivatedRoute para poder suscribir cuando cambia el parámetro del id.

  constructor(
    private clienteService: ClienteService,
    private activatedRoute: ActivatedRoute
    ) { }

  ngOnInit(): void {

    // Suscribir cuando cambia el parámetro del id para poder obtener el detalle cliente.

    this.activatedRoute.paramMap.subscribe(
      params => {
        let id: number = +params.get('id');

        // Si el id existe obtenemos el cliente mediante la clase Service.

        if (id) {
          this.clienteService.getCliente(id).subscribe(
            cliente => {
              this.cliente = cliente;
            }
          );
        }
      }
    );
  }

  /**
   * Seleccionar foto.
   * @param event
   */

  public seleccionarFoto(event) {
    this.fotoSeleccionada = event.target.files[0];
    console.log(this.fotoSeleccionada);
  }

  public subirFoto() {
    this.clienteService.subirFoto(this.fotoSeleccionada, this.cliente.id).subscribe(
      cliente => {
        this.cliente = cliente;
        Swal.fire('La foto se ha subido completamente', `La foto se ha subido con éxito: ${this.cliente.foto}`, 'success');
      }
    );
  }
}
