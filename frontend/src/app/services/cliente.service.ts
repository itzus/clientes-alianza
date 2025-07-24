import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, ClienteFilter, PageResponse } from '../models/cliente.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private apiUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) { }

  /**
   * Crea un nuevo cliente
   * @param cliente Datos del cliente a crear
   * @returns Observable con el cliente creado
   */
  createCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  /**
   * Obtiene todos los clientes con paginación
   * @param page Número de página
   * @param size Tamaño de página
   * @param sortBy Campo por el que ordenar
   * @param sortDir Dirección de ordenación
   * @returns Observable con la respuesta paginada
   */
  getClientes(page: number = 0, size: number = 10, sortBy: string = 'id', sortDir: string = 'asc'): Observable<PageResponse<Cliente>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<Cliente>>(this.apiUrl, { params });
  }

  /**
   * Busca un cliente por su shared key
   * @param sharedKey Shared key del cliente
   * @returns Observable con el cliente encontrado
   */
  getClienteBySharedKey(sharedKey: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/search/shared-key/${sharedKey}`);
  }

  /**
   * Busca clientes por filtros
   * @param filter Filtros de búsqueda
   * @param page Número de página
   * @param size Tamaño de página
   * @param sortBy Campo por el que ordenar
   * @param sortDir Dirección de ordenación
   * @returns Observable con la respuesta paginada
   */
  searchClientes(filter: ClienteFilter, page: number = 0, size: number = 10, sortBy: string = 'id', sortDir: string = 'asc'): Observable<PageResponse<Cliente>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.post<PageResponse<Cliente>>(`${this.apiUrl}/search`, filter, { params });
  }

  /**
   * Exporta los clientes a CSV
   * @returns Observable con el blob del archivo CSV
   */
  exportClientesToCsv(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export/csv`, { responseType: 'blob' });
  }
}