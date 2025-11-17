import { Component, Input, OnInit } from '@angular/core';
import { SubastasService } from '../../../services/subastas.service';
import { PagosService } from '../../../services/pagos.service';
import { AuthService } from '../../../services/auth.service';
import { UsuariosService } from '../../../services/usuarios.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../../user';
import { Puja } from '../../../puja';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-martillero-subasta',
  standalone: true,
  imports: [CurrencyPipe, CommonModule],
  templateUrl: './martillero-subasta.component.html',
  styleUrl: './martillero-subasta.component.css'
})
export class MartilleroSubastaComponent implements OnInit {

  isMartillero: boolean = false;
  isModalOpen: boolean = false;
  usuarioConsultadoPorMartillero: User | null = null;

  pujasParaMartillero: Puja[] = [];

  @Input() subastaId!: number;

  constructor(
      private subastaService: SubastasService,
      private authService: AuthService
  ) {}
  
  ngOnInit(): void {
      this.authService.usuarioActual$.subscribe(usuario => {
      if (usuario) {
        this.isMartillero = usuario.rol === 'martillero';
        if (this.isMartillero && this.subastaId) {
          this.cargarPujasParaMartillero();
        }
      }
    });
  }

    verUsuario(id: number) {
    this.authService.getUsuarioById(id).subscribe({
      next: (usuario) => {
        this.usuarioConsultadoPorMartillero = usuario;
        this.isModalOpen = true;
        console.log(this.isModalOpen)
        console.log(this.usuarioConsultadoPorMartillero)
      },
      error: (error) => {
        console.error('Error obteniendo el usuario', error);
      }
    })
  }

  rechazarPuja(id: number) {
    this.subastaService.eliminarPuja(id).subscribe({
      next: (response) => {
        console.log('Eliminada: ', response)
        this.cargarPujasParaMartillero();
      },
      error: (error) => {
        console.error('Error al eliminar la puja', error)
      }
    })
  }

  cargarPujasParaMartillero() {
      if (this.subastaId) {
        this.subastaService.getPujasSubastaId(this.subastaId).subscribe({
          next: (response) => {
            this.pujasParaMartillero = response;
            console.log('Pujas: ', response)
          },
          error: (error) => {
            console.error('Error al traer las pujas:', error)
          }
        })
    }
  }

  cerrarModal() {
    this.isModalOpen = false;
    this.usuarioConsultadoPorMartillero = null;
  }
}
