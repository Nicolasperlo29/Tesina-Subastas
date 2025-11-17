import { Injectable } from '@angular/core';
import { PujaDTOPost } from './puja-dtopost';
import { Subasta } from './subasta';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SubastaHelperService {

  constructor(private router: Router) { }

    validarPuja(puja: PujaDTOPost, subasta: Subasta, pujaMasAlta: number, realizoElDeposito: boolean): string | null {
    if (puja.userId === 0) {
      alert('Debes estar logeado');
      this.router.navigate(['/login']);
      return 'Debes estar logeado para pujar.';
    }

    if (puja.userId === subasta.userId) {
      return 'No puedes pujar en tus subastas.';
    }

    // if (!realizoElDeposito) {
    //   return 'Primero debes realizar el deposito de garantia.';
    // }

    const precioInicial = subasta.precioInicial ?? 0;
    const incrementoFijo = subasta.incrementoFijo ?? 0;

    if (puja.valor <= pujaMasAlta || puja.valor <= precioInicial) {
      return 'La puja debe ser mayor a la actual y al precio inicial.';
    }

    if (puja.valor < pujaMasAlta + incrementoFijo || puja.valor < precioInicial + incrementoFijo) {
      return 'La puja debe ser mayor a la actual y al precio inicial. Ten en cuenta el incremento.';
    }

    return null;
  }
}
