import { Component, OnDestroy, OnInit } from '@angular/core';
import { SubastasService } from '../../../services/subastas.service';
import { Subasta } from '../../../subasta';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Puja } from '../../../puja';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../user';
import { PujaDTOPost } from '../../../puja-dtopost';
import { PagosService } from '../../../services/pagos.service';
import { of, switchMap, tap } from 'rxjs';
import { UsuariosService } from '../../../services/usuarios.service';
import { MartilleroSubastaComponent } from "../martillero-subasta/martillero-subasta.component";
import { PagosSubastaComponent } from "../pagos-subasta/pagos-subasta.component";
import { SubastaHelperService } from '../../../subasta-helper.service';
import { PujaAutomaticaComponent } from "../../VistaSubasta/puja-automatica/puja-automatica.component";

@Component({
  selector: 'app-subasta',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, MartilleroSubastaComponent, PagosSubastaComponent, PujaAutomaticaComponent],
  templateUrl: './subasta.component.html',
  styleUrl: './subasta.component.css'
})
export class SubastaComponent implements OnInit, OnDestroy {

  subasta?: Subasta;
  pujaMasAlta: number = 0;
  subastaId: number = 0; // Id de subasta
  errorMessage: string = '';
  userId: number = 0; // Usuario actual
  usuarioGanando: User | null = null; // Usuario que va ganando la subasta
  vendedor?: User;
  ganador: boolean = false;
  hayPujas: boolean = false;
  // isMartillero: boolean = false;
  martillero?: User;

  pujaUserIdMasAlta: number = 0;
  // sinPujas: boolean = true;

  // mensajeNotificacion: string = "";

  // usuarioConsultadoPorMartillero: User | null = null;
  isModalOpen = false;
  isModalPujaAutomaticaOpen = false;

  // pujasParaMartillero: Puja[] = [];

  tiempoRestante: string = '';
  private intervalId: any;
  subastaEnCurso: boolean = false;

  // pagado: string = "";
  deposito: number = 10;
  realizoElDeposito: boolean = false;

  activarNotificacionGanador: boolean = false;

  imagenPrincipal: string = ''; 

  puja: PujaDTOPost = {
    subastaId: 0,
    userId: 0,
    valor: 0,
    estado: "aceptada"
  }

  constructor(
    private subastaService: SubastasService,
    private helperService: SubastaHelperService,
    private authService: AuthService,
    private userService: UsuariosService,
    private route: ActivatedRoute,
    private router: Router  
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUsuario();
    
    if (user) {
      this.userId = user.id;
    }

    this.subastaId = Number(this.route.snapshot.paramMap.get('id'));

    if (!this.subastaId) {
      console.error('ID de subasta inválido');
      return;
    }

    this.loadSubasta();
    this.loadPuja();
  }

  private loadSubasta(): void {
    this.subastaService.getSubastaById(this.subastaId).pipe(
      tap((subasta) => {
        console.log('Subasta: ', subasta);
        this.subasta = subasta;
        if (this.subasta?.imagenes && this.subasta.imagenes.length > 0) {
          this.imagenPrincipal = this.subasta.imagenes[0]; // inicializar con la primera imagen
        }
        this.iniciarContador();
        this.loadVendedor(subasta.userId);
        console.log('Incremento: ', this.subasta?.incrementoFijo)
      }),
      switchMap((subasta) => {
        if (subasta?.martilleroId != null) {
          return this.authService.getUsuarioById(subasta.martilleroId);
        } else {
          return of(null);
        }
      })
    ).subscribe({
      next: (martillero) => {
        if (martillero) {
          this.martillero = martillero;
        }
      },
      error: (err) => console.error('Error cargando subasta o martillero:', err)
    });
  }

  private loadVendedor(userId: number): void {
    this.authService.getUsuarioById(userId).subscribe({
      next: (user) => this.vendedor = user,
      error: (err) => console.error('Error cargando vendedor:', err)
    });
  }

  private loadPuja(): void {
    this.subastaService.getPujaBySubastaId(this.subastaId).subscribe({
      next: (puja) => {
        if (!puja) {
          this.pujaMasAlta = 0;
          return;
        }

        this.pujaMasAlta = puja.valor;
        this.pujaUserIdMasAlta = puja.userId;
        this.hayPujas = true;

        this.authService.getUsuarioById(puja.userId).subscribe({
          next: (user) => this.usuarioGanando = user,
          error: (err) => console.error('Error cargando usuario ganador:', err)
        });
      },
      error: (err) => console.error('Error cargando puja:', err)
    });
  }

iniciarContador() {
  this.actualizarEstadoSubasta();

  // Luego iniciar el intervalo para actualizar cada segundo
  this.intervalId = setInterval(() => {
    this.actualizarEstadoSubasta();
  }, 1000);
}

actualizarEstadoSubasta() {
  if (!this.subasta?.fechaInicio || !this.subasta?.fechaFin) return;

  const ahora = new Date();
  const inicio = new Date(this.subasta.fechaInicio);
  const fin = new Date(this.subasta.fechaFin);

  if (ahora < inicio && this.subasta.estado) {
    this.manejarSubastaNoIniciada(inicio);
  } else if (ahora >= inicio && ahora < fin) {
    this.manejarSubastaEnCurso(fin);
  } else {
    this.manejarSubastaFinalizada(fin);
  }
}

  private calcularTiempoRestante(fecha: Date): { horas: number, minutos: number, segundos: number } {
  const ahora = new Date();
  const diferencia = fecha.getTime() - ahora.getTime();
  return {
    horas: Math.floor(diferencia / (1000 * 60 * 60)),
    minutos: Math.floor((diferencia % (1000 * 60 * 60)) / (1000 * 60)),
    segundos: Math.floor((diferencia % (1000 * 60)) / 1000),
  };
}

private manejarSubastaNoIniciada(inicio: Date) {
  const { horas, minutos, segundos } = this.calcularTiempoRestante(inicio);
  this.tiempoRestante = `Comienza en: ${horas}h ${minutos}m ${segundos}s`;
  this.subastaEnCurso = false;
}

private manejarSubastaEnCurso(fin: Date) {
  const { horas, minutos, segundos } = this.calcularTiempoRestante(fin);
  this.tiempoRestante = `Finaliza en: ${horas}h ${minutos}m ${segundos}s`;
  this.subastaEnCurso = true;
}

private manejarSubastaFinalizada(fin: Date) {
  this.tiempoRestante = 'Finalizado el: ' + fin.toLocaleDateString();
  this.subastaEnCurso = false;
  clearInterval(this.intervalId);

  if (this.pujaMasAlta > 0) {
    this.actualizarGanador(this.subasta!.id, this.pujaUserIdMasAlta);
    // this.ganador = true;
  }

  if (this.subasta?.userGanadorId != null) {
    this.ganador = this.pujaUserIdMasAlta != null;
  }
  // else {
  //   this.sinPujas = true;
  // }

  if (this.userId === this.pujaUserIdMasAlta) {
    console.log('🎉 Ganaste la subasta');
    // this.actualizarGanador(this.subasta!.id, this.pujaUserIdMasAlta);
    this.activarNotificacionGanador = true;
  }

  this.pujaUserIdMasAlta = 0;
}

actualizarGanador(subastaId: number, ganadorId: number) {
  this.subastaService.actualizarGanador(subastaId, ganadorId).subscribe({
    next: (user) => {
      this.ganador = true;
      console.log('Actualizado')
    },
    error: (err) => console.error('Error cargando usuario ganador:', err)
  })
}

pujaPendiente: boolean = false;

actualizarDeposito(valor: boolean) {
  console.log('Deposito: ', valor)
  this.realizoElDeposito = valor;
}

  pujar() {
    this.puja.subastaId = this.subastaId;
    this.puja.userId = this.userId;

    if (!this.subasta) {
      return
    }

    const error = this.helperService.validarPuja(this.puja, this.subasta!, this.pujaMasAlta, this.realizoElDeposito);

    if (error) {
      this.errorMessage = error;
      return;
    }
  
    this.isModalOpen = true;
    this.pujaPendiente = true;
    this.loadSubasta();
  }

  aceptarPuja() {
     if (!this.pujaPendiente) return;

    this.subastaService.crearPuja(this.puja).subscribe({
      next: (response) => {
        this.pujaMasAlta = response.valor;
        this.errorMessage = '';
        this.hayPujas = true;
        this.pujaUserIdMasAlta = response.userId;
        this.authService.getUsuarioById(this.pujaUserIdMasAlta).subscribe({
          next: (usuario) => {
            this.usuarioGanando = usuario;
            this.isModalOpen = false;
            this.puja.valor = 0;
            this.loadSubasta();
            setTimeout(() => {
              this.loadPuja();
            }, 5000);

          },
          error: (error) => {
            console.error('Error obteniendo usuario ganador', error);
          }
        });
        console.log('Puja creada: ', response)
      },
      error: (error) => {
        console.error('Error', error)
      }
    })
  }

  cerrarModal() {
    this.isModalOpen = false;
    this.pujaPendiente = false;
    this.puja.valor = 0;
  }

  pujaAutomatica() {
    if (this.subasta?.userId == this.userId) {
      alert('No puedes pujar en tus subastas.');
      return;
    }

    this.isModalPujaAutomaticaOpen = true;
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);
  }
}
