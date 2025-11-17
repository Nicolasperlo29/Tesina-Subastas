import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InformeVendedor } from './informe-vendedor';
import { ReportePujas } from './reporte-pujas';

@Injectable({
  providedIn: 'root'
})
export class InformeVendedorService {

  private apiUrl = 'http://localhost:8083/api/informe/vendedores';
  private apiUrlReporte = 'http://localhost:8083/api/informe/pujas-por-usuario';

  constructor(private http: HttpClient) { }

  obtenerInforme(): Observable<InformeVendedor[]> {
    return this.http.get<InformeVendedor[]>(this.apiUrl);
  }

  getReportePujas(): Observable<ReportePujas[]> {
    return this.http.get<ReportePujas[]>(this.apiUrlReporte);
  }
}
