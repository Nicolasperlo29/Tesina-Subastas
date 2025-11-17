import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { User } from './user';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
   const userJson = localStorage.getItem('user');

  if (!userJson) {
    router.navigate(['/login']);
    return false;
  }

  try {
    const decoded = JSON.parse(userJson);

    if (decoded.rol === 'admin') {
      return true;
    } else {
      router.navigate(['/']);
      return false;
    }
  } catch (error) {
    router.navigate(['/']);
    return false;
  }
};
