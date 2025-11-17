import { Component, HostListener, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { User } from './user';
import { NotificationDTO } from './notificationDTO';
import { NotificationService } from './services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, DatePipe, SlicePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  
  logeado: boolean = false;
  isAdmin: boolean = false;
  isMartillero: boolean = false;
  usuario: User | null = null;
  notifications: NotificationDTO[] = [];
  notificationsNew: NotificationDTO[] = [];

  constructor(private service: AuthService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.service.usuarioActual$.subscribe(usuario => {
      this.logeado = usuario !== null;
      this.usuario = usuario;
  
      if (usuario) {
        this.isAdmin = usuario.rol === 'admin';
        this.isMartillero = usuario.rol === 'martillero';
        console.log('Usuario:', usuario);
        console.log('Rol:', usuario.rol);
        this.notificationService.getNotificationsUserId(usuario.id).subscribe({
          next: (response) => {
            console.log(response)
            this.notifications = response;
            this.notificationsNew = this.notifications.splice(0, 6)
          },
          error: (error) => {
            console.error('Error al traer las notificaciones', error)
          }
        })
      }
    });
  }

  mobileMenuOpen = false;
  scrolled = false;

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.scrolled = window.scrollY > 50;
  }
  
  userBalance: number = 0; // O el valor que tengas

cambiarIdioma(event: any) {
  const idioma = event.target.value;
  console.log('Idioma seleccionado:', idioma);
  // Aquí implementas el cambio de idioma
  // Ejemplo: this.translateService.use(idioma);
}
}
