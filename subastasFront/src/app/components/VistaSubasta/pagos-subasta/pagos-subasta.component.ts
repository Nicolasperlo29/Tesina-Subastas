import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { PagosService } from '../../../services/pagos.service';
import { Router } from '@angular/router';
import { UsuariosService } from '../../../services/usuarios.service';
import { CommonModule } from '@angular/common';
import { Subasta } from '../../../subasta';

@Component({
  selector: 'app-pagos-subasta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagos-subasta.component.html',
  styleUrl: './pagos-subasta.component.css'
})
export class PagosSubastaComponent implements OnInit, OnChanges {

  @Input() subasta?: Subasta;
  @Input() usuarioGanando: any;
  @Input() userId: number = 0;
  @Input() deposito: number = 0;
  @Input() pujaMasAlta: number = 0;
  @Input() activarNotificacion: boolean = false;
  @Input() subastaEnCurso?: boolean;

  errorMessage: string | null = null;
  mensajeNotificacion: string = "";
  pagado: string = "";
  @Output() realizoElDeposito = new EventEmitter<boolean>();
  depositoHecho: boolean = false;

  constructor(private pagosService: PagosService, private userService: UsuariosService, private router: Router) {}

  ngOnInit(): void {
    // console.log('Subasta: ', this.subasta)
    // if (this.subasta != null) {
    //   this.pagosService.getDeposito(this.subasta.id, this.userId).subscribe({
    //     next: (estado) => {
    //       console.log('Estado del deposito: ', estado)
    //       this.depositoHecho = estado;
    //       this.realizoElDeposito.emit(estado);
    //     },
    //     error: (err) => console.error('Error cargando deposito:', err)
    //   })
    //   this.pagosService.getEstadoPago(this.subasta.id).subscribe({
    //   next: (estado) => {
    //     this.pagado = estado;
    //     console.log("Estado del pago: ", this.pagado)
    //   },
    //   error: (err) => console.error('Error cargando el estado: ', err)
    // });
    // }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['activarNotificacion'] && this.activarNotificacion) {
      this.notificarPago();
    }

    console.log('Subasta: ', this.subasta)
    if (this.subasta != null) {
      this.pagosService.getDeposito(this.subasta.id, this.userId).subscribe({
        next: (estado) => {
          console.log('Estado del deposito: ', estado)
          this.depositoHecho = estado;
          this.realizoElDeposito.emit(estado);
        },
        error: (err) => console.error('Error cargando deposito:', err)
      })
      this.pagosService.getEstadoPago(this.subasta.id).subscribe({
      next: (estado) => {
        this.pagado = estado;
        console.log("Estado del pago: ", this.pagado)
      },
      error: (err) => console.error('Error cargando el estado: ', err)
    });
    }
  }

  notificarPago() {
    this.mensajeNotificacion = "¡Felicidades! Has ganado la subasta. Por favor, realiza el pago para completar la transacción.";

    console.log('Usuario ganador: ', this.usuarioGanando?.email)
    if (this.usuarioGanando) {
      this.userService.notificarUsuarioGanador(this.usuarioGanando.email, this.usuarioGanando.id).subscribe({
        next: (respuesta) => console.log('Notificación enviada:', respuesta),
        error: (err) => console.error('Error al enviar la notificación:', err)
      });
    }
  }

  depositoGarantia() {
    if (this.userId == 0) {
      alert('Debes estar logeado')
      this.errorMessage = 'Debes estar logeado.';
      this.router.navigate(['/login']);
      return
    }

    if (this.subasta?.userId == this.userId) {
      this.errorMessage = 'No puedes realizar el deposito en tu propia subasta'
      return
    }
    if (this.subasta && this.userId) {
      this.pagosService.crearDepositoPago(this.subasta?.id.toString(), this.userId, this.deposito).subscribe({
        next: (response) => {
          console.log('Respuesta del backend:', response);
          if (response && response.initPoint) {
            window.location.href = response.initPoint; // Redirigir a MercadoPago
          } else {
            console.error('No se recibió la URL de pago');
          }
        },
        error: (error) => {
          console.error('Error al generar el pago', error);
        }
      });
    }
  }

    realizarPago() {
    if (this.subasta && this.usuarioGanando) {
      this.pagosService.crearPreferenciaPago(this.subasta?.id.toString(), this.usuarioGanando.id, this.subasta?.title, this.pujaMasAlta).subscribe({
        next: (response) => {
          console.log('Respuesta del backend:', response);
          if (response && response.initPoint) {
            window.location.href = response.initPoint; // Redirigir a MercadoPago
          } else {
            console.error('No se recibió la URL de pago');
          }
          this.notificarPago();
        },
        error: (error) => {
          console.error('Error al generar el pago', error);
        }
      });
    }
  }

  verDetalle() {
    this.router.navigate(['/profile']);
  }
}
