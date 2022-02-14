import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  modal: boolean = false;

  constructor() { }

  // Abrir ventana modal.

  public abrirModal() {
    console.log('DEBUG: abriendo modal desde modal.service')
    this.modal = true;
  }

  // Cerrar ventana modal.

  public cerrarModal() {
    console.log('DEBUG: cerrando modal desde modal.service.ts')
    this.modal = false;
  }
}
