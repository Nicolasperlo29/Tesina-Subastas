import { Component, OnInit } from '@angular/core';
import { NotificationDTO } from '../../notificationDTO';
import { NotificationService } from '../../services/notification.service';
import { User } from '../../user';
import { UsuariosService } from '../../services/usuarios.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit {

  user: User | null = null;
  notifications: NotificationDTO[] = [];

  constructor(private notificationService: NotificationService, private service: AuthService) {}
  
  ngOnInit(): void {
    this.service.usuarioActual$.subscribe(usuario => {
      this.user = usuario;
    });
    
    if (this.user) {
      this.cargarNotificaciones(this.user.id)
    }
  }

  cargarNotificaciones(userId: number) {
    this.notificationService.getNotificationsUserId(userId).subscribe({
      next: (response) => {
        this.notifications = response;
        console.log('Notificaciones: ', response)
      },
      error: (error) => {
        console.error('Error al traer las notificaciones:', error)
      }
    })
  }

  ocultar(id: number) {
    this.notificationService.ocultarNotificacion(id).subscribe({
        next: (response) => {
          console.log('Ocultada: ', response)
          if (this.user) {
            this.cargarNotificaciones(this.user?.id)
          }
        },
        error: (error) => {
          console.error('Error al ocultar la notificacion:', error)
        }
    })
  }
}
