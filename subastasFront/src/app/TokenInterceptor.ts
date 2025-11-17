import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('token'); // Obtener el token del localStorage

    if (token) {
      // Si el token existe, agregamos el Authorization header
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next.handle(cloned);  // Enviar la solicitud con el token
    }
    return next.handle(req); // Si no hay token, enviamos la solicitud sin el header
  }
}