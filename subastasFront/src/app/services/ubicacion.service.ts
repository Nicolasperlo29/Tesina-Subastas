import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UbicacionService {

  constructor(private http: HttpClient) { }

  private apiUrl = 'https://countriesnow.space/api/v0.1/countries/cities';

  getCiudades(pais: string) {
    return this.http.post<any>(this.apiUrl, { country: pais });
  }
}
