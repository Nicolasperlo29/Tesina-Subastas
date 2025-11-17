import { Component, OnInit } from '@angular/core';
import { UserDTOPostEdit } from '../../user-dtopost-edit';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { UsuariosService } from '../../services/usuarios.service';
import { FormsModule } from '@angular/forms';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Puja } from '../../puja';
import { SubastasService } from '../../services/subastas.service';
import { User } from '../../user';
import { Pago } from '../../pago';
import { PagosService } from '../../services/pagos.service';
import { Subasta } from '../../subasta';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule, CurrencyPipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  usuarioEditar?: UserDTOPostEdit;
  usuario: User | null = null;
  isEditMode: boolean = false;
  profileFields: { label: string; placeholder: string | undefined }[] = [];
  pujas: Puja[] = [];
  isModalOpen: boolean = false;
  isModalPujaOpen: boolean = false;
  pujaParaEliminarId: number = 0;
  subastasGanadas: Subasta[] = [];
  textoEliminar: string = '';
  isModalErrorOpen: boolean = false;

  showModal = false;
  showButtons = true;
  modalMessage = '';
  onConfirmCallback: (() => void) | null = null;

  puja?: Puja;

  pagos: Pago[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private userService: UsuariosService,
    private pagoService: PagosService,
    private subastaService: SubastasService
  ) { }

  ngOnInit(): void {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      this.usuario = JSON.parse(storedUser);
      this.usuarioEditar = JSON.parse(storedUser);
      this.profileFields = [
        { label: 'Nombre', placeholder: this.usuarioEditar?.name },
        { label: 'Apellido', placeholder: this.usuarioEditar?.lastname },
        { label: 'Nick Name', placeholder: this.usuarioEditar?.username },
        { label: 'País', placeholder: 'Argentina' },
        { label: 'Número de teléfono', placeholder: this.usuarioEditar?.numberphone }
      ];
    }
    if (this.usuario) {
      this.cargarPujas(this.usuario?.id);
      this.cargarSubastasGanadas(this.usuario.id)
      this.pagoService.getPagosPorUsuario(this.usuario?.id).subscribe({
        next: (res) => {
          this.pagos = res;
          console.log(res)
        },
        error: (err) => {
          console.error('Error al obtener pagos:', err);
        }
      });
    }
  }

  cargarPujas(userId: number) {
    this.subastaService.getPujasIdUsuario(userId).subscribe({
      next: (response) => {
        this.pujas = response;
        console.log('Pujas: ', response)
      },
      error: (error) => {
        console.error('Error al traer las pujas:', error)
      }
    })
  }

  cargarSubastasGanadas(userId: number) {
    this.subastaService.getSubastasGanadas(userId).subscribe({
      next: (response) => {
        this.subastasGanadas = response;
        console.log('Subastas: ', response)
      },
      error: (error) => {
        console.error('Error al traer las subastas:', error)
      }
    })
  }

  editar() {
    this.isEditMode = true;
  }

  cancelarEdicion() {
    this.isEditMode = false;
  }

  guardarEdicion() {
    if (!this.usuarioEditar) return;

    const nombre = this.profileFields[0].placeholder?.trim() || '';
    const apellido = this.profileFields[1].placeholder?.trim() || '';
    const username = this.profileFields[2].placeholder?.trim() || '';
    const numberphone = this.profileFields[4].placeholder?.trim() || '';

    const soloLetrasRegex = /^[A-Za-zÁÉÍÓÚáéíóúñÑ\s]+$/;

    if (!nombre || !soloLetrasRegex.test(nombre)) {
      this.openModal('El nombre es obligatorio y solo debe contener letras.', () => { });
      this.showButtons = false;
      return;
    }

    if (!apellido || !soloLetrasRegex.test(apellido)) {
      this.openModal('El apellido es obligatorio y solo debe contener letras.', () => { });
      this.showButtons = false;
      return;
    }

    this.openModal('¿Guardar cambios?', () => {
      const updatedUser: UserDTOPostEdit = {
        name: nombre,
        lastname: apellido,
        username: username,
        numberphone: numberphone
      };

      this.userService.editarUsuario(this.usuario!.id, updatedUser).subscribe({
        next: (updated) => {
          this.usuarioEditar = updated;
          this.authService.getUsuarioById(this.usuario!.id).subscribe({
            next: (response) => {
              this.usuario = response;
              localStorage.setItem('user', JSON.stringify(response));
            },
            error: (error) => {
              console.error('Error al traer las pujas:', error);
            }
          });
          this.isEditMode = false;
        },
        error: (err) => {
          this.showButtons = false;
          this.openModal('El nombre de usuario ya está en uso.', () => { });
        }
      });
    });
  }

  openModal(message: string, onConfirm: () => void) {
    this.modalMessage = message;
    this.onConfirmCallback = onConfirm;
    this.showModal = true;
  }

  // Cuando el usuario confirma
  confirm() {
    if (this.onConfirmCallback) this.onConfirmCallback();
    this.closeModal();
  }

  // Cuando cancela o cierra el modal
  closeModal() {
    this.showModal = false;
    this.modalMessage = '';
    this.onConfirmCallback = null;
  }

  openModalEliminarCuenta() {
    this.isModalOpen = true;
  }

  eliminarCuenta() {
    if (!this.usuario) {
      return
    }

    if (this.textoEliminar.trim().toLowerCase() === 'confirmar') {
      this.userService.darDeBajaUsuario(this.usuario?.email).subscribe({
        next: (res) => {
          if (res) {
            this.router.navigate(['/'])
            this.authService.logout();
          } else {
            alert('No se encontró el usuario o ya está dado de baja');
          }
          this.cerrarModal();
        },
        error: (err) => {
          alert('Error al dar de baja el usuario');
          console.error(err);
        }
      });
    }
  }

  abrirModalPuja(id: number) {
    this.pujaParaEliminarId = id;

    this.puja = this.pujas.find(p => p.id === id);

    const fechaEpochSegundos = this.puja?.fechaCreada;

    if (fechaEpochSegundos !== undefined) {
      const fecha = new Date(Number(fechaEpochSegundos) * 1000);
      const ahora = new Date();
      console.log(fecha.toISOString());

      const diferenciaMinutos = (ahora.getTime() - fecha.getTime()) / 60000; // milisegundos a minutos

      if (diferenciaMinutos > 10) {
        this.isModalErrorOpen = true;
        return;
      }
      else {
        this.isModalPujaOpen = true;
      }
    } else {
      alert('fechaCreada es undefined');
    }

    console.log('Puja: ', this.puja);
  }

  cancelarPuja() {
    this.subastaService.eliminarPuja(this.pujaParaEliminarId).subscribe({
      next: (response) => {
        console.log('Eliminada: ', response)
        if (this.usuario) {
          this.cargarPujas(this.usuario?.id);
        }
        this.isModalPujaOpen = false;
      },
      error: (error) => {
        console.error('Error al eliminar la puja', error)
      }
    })
  }

  cerrarModal() {
    this.isModalOpen = false;
    this.isModalPujaOpen = false;
    this.isModalErrorOpen = false;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getInitials(): string {
    if (!this.usuarioEditar?.name || !this.usuarioEditar?.lastname) {
      return 'U';
    }
    return (this.usuarioEditar.name.charAt(0) + this.usuarioEditar.lastname.charAt(0)).toUpperCase();
  }

  calcularTotalInvertido(): number {
    return this.pagos.reduce((total, pago) => total + pago.monto, 0);
  }

  calcularTotalPagos(): number {
    return this.pagos.reduce((total, pago) => total + pago.monto, 0);
  }
}
