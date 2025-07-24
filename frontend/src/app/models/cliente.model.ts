export interface Cliente {
  id?: number;
  sharedKey: string;
  nombre: string;
  telefono: string;
  email: string;
  fechaInicio: string;
  fechaFin: string;
  fechaCreacion?: string;
}

export interface ClienteFilter {
  sharedKey?: string;
  nombre?: string;
  telefono?: string;
  email?: string;
  fechaInicio?: string;
  fechaFin?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}