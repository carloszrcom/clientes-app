import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'paginator-nav',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.css']
})
export class PaginatorComponent implements OnInit {

  @Input() paginado: any;

  paginas: number[];

  constructor() { }

  ngOnInit(): void {

    // fill() llena el array con datos.
    // map modifica los 0 con los números de páginas.
    this.paginas = new Array(this.paginado.totalPages).fill(0).map(
      // Con _ indicamos que no estamos utilizando el valor.
      (_valor, indice) => indice + 1
    );
  }
}
