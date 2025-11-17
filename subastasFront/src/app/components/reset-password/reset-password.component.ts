import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UsuariosService } from '../../services/usuarios.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent implements OnInit {

  newPassword = '';
  token = '';
  message = '';
  error = '';

  constructor(private route: ActivatedRoute, private userService: UsuariosService) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  submit() {
    this.userService.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.message = 'Contraseña actualizada con éxito.',
        console.log('Mensaje: ', this.message)
      },
      error: (error) => {
        console.log(error)
        this.error = 'Error al actualizar la contraseña.'
      }
    });
  }
}
