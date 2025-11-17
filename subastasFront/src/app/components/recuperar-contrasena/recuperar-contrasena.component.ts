import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UsuariosService } from '../../services/usuarios.service';

@Component({
  selector: 'app-recuperar-contrasena',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './recuperar-contrasena.component.html',
  styleUrl: './recuperar-contrasena.component.css'
})
export class RecuperarContrasenaComponent {

    email: string = '';
    password: string = '';
    error: string = '';
    message: string = '';
  
    constructor(private userService: UsuariosService, private router: Router) {}
  
    onSubmit() {
      this.userService.requestPasswordReset(this.email).subscribe({
        next: () => {
          this.message = 'Revisa tu correo para restablecer tu contraseña.',
          console.log('Mensaje: ', this.message)
        },
        error: (error) => {
          this.error = 'Error al enviar el email.'
          console.log('Error: ', error)
        }
      });
    }
}
