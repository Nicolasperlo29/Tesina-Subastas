import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subasta } from '../subasta';
import { Observable } from 'rxjs';
import { Puja } from '../puja';
import { SubastaDTO } from '../subasta-dto';
import { PujaDTOPost } from '../puja-dtopost';
import { SubastaDTOEdit } from '../subasta-dtoedit';
import { PujaAutomaticaDTOPost } from '../puja-automatica-dtopost';

@Injectable({
  providedIn: 'root'
})
export class SubastasService {

  private urlSubastas = 'http://localhost:8083/subastas/';

  private urlPujas = 'http://localhost:8083/pujas/';

  constructor(private http: HttpClient) { }

  getSubastasIdUsuario(idUsuario: number) {
    return this.http.get<Subasta[]>(this.urlSubastas + "getSubasta/" + idUsuario);
  }

  getSubastaById(id: number) {
    return this.http.get<Subasta>(this.urlSubastas + "subasta/" + id);
  }

  getSubastasActivas() {
    return this.http.get<SubastaDTO[]>(this.urlSubastas + "activas");
  }

  getAllSubastas() {
    return this.http.get<Subasta[]>(this.urlSubastas + "getAllSubastas");
  }

  getSubastasCategoriaEstado(categoria: string, estado: string) {
    return this.http.get<Subasta[]>(this.urlSubastas + "getSubastas/" + estado + "/" + categoria);
  }

  getSubastasMartilleroId(martilleroId: number) {
    return this.http.get<Subasta[]>(this.urlSubastas + "getSubastas/martillero/" + martilleroId);
  }

  getSubastasPorEstado(estado: string): Observable<Subasta[]> {
    return this.http.get<Subasta[]>(this.urlSubastas + "estado/" + estado);
  }

  getSubastasGanadas(idUsuario: number) {
    return this.http.get<Subasta[]>(this.urlSubastas + "subasta/userId/" + idUsuario);
  }

  actualizarSubasta(id: number, subasta: SubastaDTOEdit): Observable<string> {
    return this.http.put(`${this.urlSubastas}${id}`, subasta, { responseType: 'text' });
  }
  
  getPujaBySubastaId(subastaId: number) {
    return this.http.get<Puja>(this.urlPujas + "puja/" + subastaId);
  }

  actualizarGanador(subastaId: number, ganadorId: number): Observable<string> {
    const url = `${this.urlSubastas}${subastaId}/ganador/${ganadorId}`;
    return this.http.put(url, null, { responseType: 'text' });
  }

  eliminarSubasta(subastaId: number): Observable<string> {
    const url = `${this.urlSubastas}ocultar/${subastaId}`;
    return this.http.put(url, null, { responseType: 'text' });
  }

  getPujasSubastaId(subastaId: number) {
    return this.http.get<Puja[]>(this.urlPujas + "getPujas/subastaId/" + subastaId);
  }

  getPujasIdUsuario(userId: number) {
    return this.http.get<Puja[]>(this.urlPujas + "pujasUserId/" + userId);
  }

  crearSubasta(subasta: SubastaDTO): Observable<SubastaDTO> {
    return this.http.post<SubastaDTO>(this.urlSubastas + "postSubasta", subasta);
  }

  crearPujaAutomatica(puja: PujaAutomaticaDTOPost): Observable<PujaAutomaticaDTOPost> {
    return this.http.post<PujaAutomaticaDTOPost>(this.urlPujas + "puja-automatica", puja);
  }

  crearPuja(puja: PujaDTOPost): Observable<PujaDTOPost> {
    return this.http.post<PujaDTOPost>(this.urlPujas + "postPuja", puja);
  }

  eliminarPuja(id: number) {
    return this.http.delete(this.urlPujas + "deletePuja/" + id);
  }
}
