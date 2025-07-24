import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService } from './cliente.service';
import { Cliente, PageResponse } from '../models/cliente.model';
import { environment } from '../../environments/environment';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/clientes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a cliente', () => {
    const mockCliente: Cliente = {
      sharedKey: 'CLI123',
      nombre: 'Cliente Prueba',
      telefono: '1234567890',
      email: 'cliente@test.com',
      fechaInicio: '2023-01-01',
      fechaFin: '2023-12-31'
    };

    const mockResponse: Cliente = {
      id: 1,
      ...mockCliente,
      fechaCreacion: '2023-01-01'
    };

    service.createCliente(mockCliente).subscribe(cliente => {
      expect(cliente).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should get clientes with pagination', () => {
    const mockResponse: PageResponse<Cliente> = {
      content: [
        {
          id: 1,
          sharedKey: 'CLI123',
          nombre: 'Cliente Prueba',
          telefono: '1234567890',
          email: 'cliente@test.com',
          fechaInicio: '2023-01-01',
          fechaFin: '2023-12-31',
          fechaCreacion: '2023-01-01'
        }
      ],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 1,
      totalPages: 1,
      last: true
    };

    service.getClientes().subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}?page=0&size=10&sortBy=id&sortDir=asc`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get cliente by shared key', () => {
    const mockResponse: Cliente = {
      id: 1,
      sharedKey: 'CLI123',
      nombre: 'Cliente Prueba',
      telefono: '1234567890',
      email: 'cliente@test.com',
      fechaInicio: '2023-01-01',
      fechaFin: '2023-12-31',
      fechaCreacion: '2023-01-01'
    };

    service.getClienteBySharedKey('CLI123').subscribe(cliente => {
      expect(cliente).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/search/shared-key/CLI123`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should search clientes by filter', () => {
    const mockFilter = {
      sharedKey: 'CLI'
    };

    const mockResponse: PageResponse<Cliente> = {
      content: [
        {
          id: 1,
          sharedKey: 'CLI123',
          nombre: 'Cliente Prueba',
          telefono: '1234567890',
          email: 'cliente@test.com',
          fechaInicio: '2023-01-01',
          fechaFin: '2023-12-31',
          fechaCreacion: '2023-01-01'
        }
      ],
      pageNumber: 0,
      pageSize: 10,
      totalElements: 1,
      totalPages: 1,
      last: true
    };

    service.searchClientes(mockFilter).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/search?page=0&size=10&sortBy=id&sortDir=asc`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockFilter);
    req.flush(mockResponse);
  });

  it('should return correct URL for CSV export', () => {
    const exportUrl = service.exportClientesToCsv();
    expect(exportUrl).toBe(`${apiUrl}/export/csv`);
  });
});