import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const loginGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userJson = localStorage.getItem('user');

  if (userJson) {
    // Ya hay usuario logueado, no puede ir a login
    router.navigate(['/profile']); // o la ruta que quieras
    return false;
  }

  // No hay usuario, puede entrar a login
  return true;
};
