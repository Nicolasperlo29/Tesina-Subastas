import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private usuarioActualSubject = new BehaviorSubject<User | null>(null);
  usuarioActual$ = this.usuarioActualSubject.asObservable();
  
  constructor(private http: HttpClient) { 
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      this.usuarioActualSubject.next(JSON.parse(storedUser));
    }
  }

  private baseUrl = 'http://localhost:8081/auth';

  login(email: string, password: string) {
    return this.http.post<{ token: string }>(`${this.baseUrl}/login`, {
      email,
      password
    });
  }

  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  setUsuario(usuario: User) {
    this.usuarioActualSubject.next(usuario);
  }

  getUsuario(): User | null {
    return this.usuarioActualSubject.value;
  }

  getUserId(): number | null {
    return this.usuarioActualSubject.value?.id || null;
  }

  // guardarToken(token: string) {
  //   localStorage.setItem('token', token);
  //   const decoded: any = jwtDecode(token);

  //   const email = decoded.sub;

  //   console.log('Decoded: ', decoded);

  //   this.getUsuarioActual(email).subscribe(usuario => {
  //     localStorage.setItem('user', JSON.stringify(usuario));
  //     this.setUsuario(usuario);
  //   });
  // }

  guardarToken(token: string): Promise<User> {
  return new Promise((resolve, reject) => {
    localStorage.setItem('token', token);
    const decoded: any = jwtDecode(token);
    const email = decoded.sub;

    this.getUsuarioActual(email).subscribe({
      next: usuario => {
        localStorage.setItem('user', JSON.stringify(usuario));
        this.setUsuario(usuario);
        resolve(usuario);
      },
      error: err => reject(err)
    });
  });
}

getUsuarioActual(email: string) {
  const headers = this.getAuthHeaders();
  console.log('Headers para getUsuarioActual:', headers.keys(), headers.get('Authorization'));
  return this.http.get<User>(
    `http://localhost:8081/user/me`,
    { headers }
  );
}

getUsuarioById(userId: number) {
  return this.http.get<User>(
    `http://localhost:8081/user/${userId}`,
    { headers: this.getAuthHeaders() }
  );
}

  getRol(): string | null {
    const user = this.obtenerUsuario();
    return user ? user.role : null;
  }

  // Verifica si el usuario es admin
  esAdmin(): boolean {
    return this.getRol() === 'admin';
  }

  // Verifica si el usuario es martillero
  esMartillero(): boolean {
    return this.getRol() === 'martillero';
  }

  obtenerUsuario() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  // getUsuarioActual(email: string) {
  //   return this.http.get<User>('http://localhost:8081/user/me/' + email);
  // }

  // getUsuarioById(userId: number) {
  //   return this.http.get<User>('http://localhost:8081/user/' + userId);
  // }

  obtenerToken(): string | null {
    return localStorage.getItem('token');
  }
  
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    this.usuarioActualSubject.next(null);
  }

  estaAutenticado(): boolean {
    return !!this.obtenerToken();
  }
}