import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../user';
import { Observable } from 'rxjs';
import { UserDTOPost } from '../user-dtopost';
import { UserDTOPostEdit } from '../user-dtopost-edit';

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {

  private url = 'http://localhost:8081/auth'
  private urlEmail = 'http://localhost:8087/notification/email/ganador'
  private urlUser = 'http://localhost:8081/user'
  private urlEmailCreadorSubasta = 'http://localhost:8081/user/notificationSubasta/email'
  private urlDeleteUserByEmail = 'http://localhost:8081/user/darDeBaja'

  constructor(private http: HttpClient) { }
  
  crearUsuario(user: UserDTOPost | undefined): Observable<UserDTOPost> {
    return this.http.post<UserDTOPost>(`${this.url}/register`, user);
  }

  editarUsuario(id: number, user: UserDTOPostEdit): Observable<UserDTOPostEdit> {
    const token = localStorage.getItem('token');

    console.log('Token: ', token)
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.put<UserDTOPostEdit>(`${this.urlUser}/editar/${id}`, user, { headers });
  }

  notificarUsuarioGanador(email: string, userId: number): Observable<string> {
    return this.http.post<string>(`${this.urlEmail}/${email}`, {userId});
  }

  consultarUsuarios(): Observable<User[]> {
    return this.http.get<User[]>(`${this.urlUser}/${'users'}`);
  }

  activarCuenta(email: string) {
    const params = new HttpParams().set('email', email);
    return this.http.post(`${this.url}/activar`, null, { params });
  }

  notificarSubastaCreada(email?: string): Observable<string> {
    return this.http.post<string>(`${this.urlEmailCreadorSubasta}/${email}`, {});
  }

  requestPasswordReset(email: string): Observable<any> {
    return this.http.post(this.url + '/recover', { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(this.url + '/reset-password', { token, newPassword });
  }

  darDeBajaUsuario(email: string) : Observable<boolean> {
    return this.http.get<boolean>(`${this.urlDeleteUserByEmail}`, {
      params: { email }
    });
  }
}
