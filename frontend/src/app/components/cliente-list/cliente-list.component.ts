import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTable } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';

import { Cliente, ClienteFilter, PageResponse } from '../../models/cliente.model';
import { ClienteService } from '../../services/cliente.service';

@Component({
  selector: 'app-cliente-list',
  templateUrl: './cliente-list.component.html',
  styleUrls: ['./cliente-list.component.scss']
})
export class ClienteListComponent implements OnInit {
  clientes: Cliente[] = [];
  displayedColumns: string[] = ['sharedKey', 'nombre', 'email', 'telefono', 'fechaInicio', 'fechaFin', 'acciones'];
  totalElements: number = 0;
  pageSize: number = 10;
  pageIndex: number = 0;
  isLoading: boolean = false;
  
  // Filtros
  filtro: ClienteFilter = {};
  sharedKeySearch: string = '';
  isSearchAdvance: boolean= false;
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<Cliente>;
  
  constructor(
    private clienteService: ClienteService,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.loadClientes();
  }
  
  loadClientes(): void {
    console.info('Inicio Carga Clientes');
    this.isLoading = true;
    this.clienteService.getClientes(this.pageIndex, this.pageSize)
      .subscribe({
        next: (response: PageResponse<Cliente>) => {
          console.log(response);
          this.clientes = response.content;
          this.totalElements = response.totalElements;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error al cargar clientes', error);
          this.snackBar.open('Error al cargar los clientes', 'Cerrar', { duration: 3000 });
          this.isLoading = false;
        }
      });
  }
  
  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadClientes();
  }
  
  buscarPorSharedKey(): void {
    if (!this.sharedKeySearch.trim()) {
      this.loadClientes();
      return;
    }
    
    this.isLoading = true;
    this.clienteService.getClienteBySharedKey(this.sharedKeySearch)
      .subscribe({
        next: (cliente) => {
          this.clientes = cliente ? [cliente] : [];
          this.totalElements = cliente ? 1 : 0;
          this.isLoading = false;
        },
        error: (error) => {
          if (error.status === 404) {
            this.clientes = [];
            this.totalElements = 0;
            this.snackBar.open('No se encontró ningún cliente con esa clave', 'Cerrar', { duration: 3000 });
          } else {
            console.error('Error al buscar cliente', error);
            this.snackBar.open('Error al buscar cliente', 'Cerrar', { duration: 3000 });
          }
          this.isLoading = false;
        }
      });
  }

  busquedaAvanzada():void{
    this.isSearchAdvance = !this.isSearchAdvance;
  }
  
  aplicarFiltros(): void {
    console.info('Aplicar filtros');
    this.isLoading = true;
    this.clienteService.searchClientes(this.filtro, this.pageIndex, this.pageSize)
      .subscribe({
        next: (response: PageResponse<Cliente>) => {
          console.log(response);
          this.clientes = response.content;
          this.totalElements = response.totalElements;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error al filtrar clientes', error);
          this.snackBar.open('Error al filtrar clientes', 'Cerrar', { duration: 3000 });
          this.isLoading = false;
        }
      });

  }
  
  limpiarFiltros(): void {
    this.filtro = {};
    this.sharedKeySearch = '';
    this.pageIndex = 0;
    this.loadClientes();
  }
  
  exportarCSV(): void {
    this.isLoading = true;
    this.clienteService.exportClientesToCsv()
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'clientes.csv';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
          this.isLoading = false;
          this.snackBar.open('Archivo CSV descargado correctamente', 'Cerrar', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error al exportar clientes', error);
          this.snackBar.open('Error al exportar clientes a CSV', 'Cerrar', { duration: 3000 });
          this.isLoading = false;
        }
      });
  }
}