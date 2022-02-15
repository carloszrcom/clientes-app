import { EventEmitter, Injectable } from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class ModalService {

  public modal: boolean = false;

  private _notificarUpload = new EventEmitter<any>();

  constructor() { }

  // getter de la propiedad modificarUpload.

  get notificarUpload(): EventEmitter<any> {
    return this._notificarUpload;
  }

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
