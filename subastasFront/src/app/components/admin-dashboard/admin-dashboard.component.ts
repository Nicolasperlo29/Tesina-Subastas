import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UsuariosService } from '../../services/usuarios.service';
import { FormsModule } from '@angular/forms';
import { User } from '../../user';
import { FilterByEmailPipe } from '../../filter-by-email.pipe';
import { InformeVendedor } from '../../informe-vendedor';
import { InformeVendedorService } from '../../informe-vendedor.service';
import { NotificationDTO } from '../../notificationDTO';
import { NotificationService } from '../../services/notification.service';
import { FilterNotificationsByEmailPipe } from '../../filter-notifications-by-email.pipe';
import { ReportePujas } from '../../reporte-pujas';
import { exportarReportePujasPDF } from '../../exportar-reporte-pujas-pdf';
import { SubastaDTO } from '../../subasta-dto';
import { SubastasService } from '../../services/subastas.service';
import { Subasta } from '../../subasta';
import { exportarReporteVendedoresPDF } from '../../exportar-informe-vendedores';
import { exportarSubastasActivasPDF } from '../../exportar-subastas-activas-pdf';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule, FilterByEmailPipe, FilterNotificationsByEmailPipe],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit{

  isAdmin: boolean = false;
  isModalOpen: boolean = false;
  emailBaja: string = '';
  usuarios: User[] = [];
  isModalUsersOpen: boolean = false;
  filtroEmail: string = "";
  filtroFecha: string = "";
  isModalNotificationsOpen: boolean = false;
  subastasActivas: SubastaDTO[] = [];

  informe: InformeVendedor[] = [];
  notifications: NotificationDTO[] = [];
  reporte: ReportePujas[] = [];

  constructor(private authService: AuthService, private userService: UsuariosService, private informeService: InformeVendedorService, private notificationService: NotificationService, private subastasService: SubastasService) {}

  ngOnInit(): void {
    const role = this.authService.getRol();
    this.isAdmin = role === 'admin';

    this.informeService.obtenerInforme().subscribe({
      next: (data) => this.informe = data,
      error: (err) => console.error('Error al cargar informe', err)
    });
    this.informeService.getReportePujas().subscribe({
      next: (data) => this.reporte = data,
      error: (err) => console.error('Error cargando reporte', err)
    });
    this.obtenerSubastasActivas();
  }

  openModal() {
    this.isModalOpen = true;
  }

  cerrarModal() {
    this.isModalOpen = false;
  }

  obtenerSubastasActivas() {
    this.subastasService.getSubastasActivas().subscribe({
      next: (res) => {
        this.subastasActivas = res;
        console.log(res)
      },
      error: (err) => {
        alert('Error');
        console.error(err);
      }
    })
  }

  consultarNotificaciones() {
    this.isModalNotificationsOpen = true;

    this.notificationService.getNotifications().subscribe({
      next: (res) => {
        this.notifications = res;
        console.log(res)
      },
      error: (err) => {
        alert('Error');
        console.error(err);
      }
    })
  }

  descargarPDFPujas(): void {
    exportarReportePujasPDF(this.reporte);
  }

  descargarPDFVendedores(): void {
    exportarReporteVendedoresPDF(this.informe);
  }

  descargarPDFSubastasActivas(): void {
    exportarSubastasActivasPDF(this.subastasActivas);
  }

  cerrarModalNotifications() {
    this.isModalNotificationsOpen = false;
  }

  confirmarBaja() {
    if (!this.emailBaja) {
      alert('Por favor, ingrese un email válido');
      return;
    }
    this.userService.darDeBajaUsuario(this.emailBaja).subscribe({
      next: (res) => {
        if (res) {
          alert('Usuario dado de baja correctamente');
        } else {
          alert('No se encontró el usuario o ya está dado de baja');
        }
        this.cerrarModal();
        this.emailBaja = '';
      },
      error: (err) => {
        alert('Error al dar de baja el usuario');
        console.error(err);
      }
    });
  }

  consultarUsuarios() {
    this.isModalUsersOpen = true;

    this.userService.consultarUsuarios().subscribe({
        next: (res) => {
          this.usuarios = res;
          console.log(this.usuarios)
      },
      error: (err) => {
        console.error('Error al traer los usuarios', err);
      }
    })
  }

  cerrarModalUsuarios() {
    this.isModalUsersOpen = false;
  }
}
