import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Pago } from '../pago';

@Injectable({
  providedIn: 'root'
})
export class PagosService {

  constructor(private http: HttpClient) { }

  private url = "http://localhost:8084/pagos";

  private urlPagos = "http://localhost:8084/pagos/crear-preferencia";

  private urlDepositoPago = "http://localhost:8084/pagos/crear-deposito";

  private urlPagosEstado = "http://localhost:8084/pagos/estado/";

  crearPreferenciaPago(subastaId: string, usuarioId: number, titulo: string, precioFinal: number): Observable<any> {
    const pagoRequest = {
      subastaId: subastaId,
      usuarioId: usuarioId,
      titulo: titulo,
      precioFinal: precioFinal
    };
    const httpOptions = {
      headers: new HttpHeaders({
          'Content-Type': 'application/json'
      })
  };
  console.log(pagoRequest)
  return this.http.post<any>(this.urlPagos, pagoRequest, httpOptions);
  
  }

  crearDepositoPago(subastaId: string, userId: number, monto: number): Observable<any> {
    const depositoRequest = {
      subastaId: subastaId,
      userId: userId,
      monto: monto
    };
    const httpOptions = {
      headers: new HttpHeaders({
          'Content-Type': 'application/json'
      })
  };
  console.log(depositoRequest)
  return this.http.post<any>(this.urlDepositoPago, depositoRequest, httpOptions);
  
  }

  getEstadoPago(subastaId: number): Observable<string> {
    return this.http.get(`http://localhost:8084/pagos/estado/${subastaId}`, { responseType: 'text' });
  }

  getPagosPorUsuario(idUsuario: number): Observable<Pago[]> {
    return this.http.get<Pago[]>(`${this.url}/${idUsuario}`);
  }

  getDeposito(subastaId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`http://localhost:8084/pagos/deposito/${subastaId}/${userId}`);
  }

}
