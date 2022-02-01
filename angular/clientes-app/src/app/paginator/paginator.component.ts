import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'paginator-nav',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.css']
})
export class PaginatorComponent implements OnInit, OnChanges {

  @Input() paginado: any;

  paginas: number[];

  desde: number;
  hasta: number;

  constructor() { }

  ngOnInit(): void {

    this.initPaginator();
  }
  
  ngOnChanges(changes: SimpleChanges): void {

    let paginadoActualizado = changes['paginado'];

    if (paginadoActualizado.previousValue) {
      this.initPaginator();
    }
  }
  
  private initPaginator(): void {
    
    this.desde = Math.min(Math.max(1, this.paginado.number - 4) , this.paginado.totalPages - 5);
    this.hasta = Math.max(Math.min(this.paginado.totalPages, this.paginado.number + 4), 6); 
  
    // Calcular rango de páginas.
    if (this.paginado.totalPages > 5) {
  
      this.paginas = new Array(this.hasta - this.desde + 1).fill(0).map(
        // Con _ indicamos que no estamos utilizando el valor.
        (_valor, indice) => indice + this.desde
      );
      
    } else {
  
      // fill() llena el array con datos.
      // map modifica los 0 con los números de páginas.
      this.paginas = new Array(this.paginado.totalPages).fill(0).map(
        // Con _ indicamos que no estamos utilizando el valor.
        (_valor, indice) => indice + 1
      );
    }
  }
}
