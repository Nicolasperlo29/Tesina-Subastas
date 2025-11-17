import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { jwtDecode } from 'jwt-decode';
import { User } from '../../user';
import { LoginRequest } from '../../login-request';
import { firstValueFrom } from 'rxjs';
import { CommonModule } from '@angular/common';
import { UserDTOPostEdit } from '../../user-dtopost-edit';
import { UsuariosService } from '../../services/usuarios.service';
import { Puja } from '../../puja';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  error = '';
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  async onSubmit() {
    if (this.loginForm.invalid) return;

    try {
      const { email, password } = this.loginForm.value as LoginRequest;
      const response = await firstValueFrom(this.authService.login(email, password));

      if (response.token) {
        const usuario = await this.authService.guardarToken(response.token);
        localStorage.setItem('user', JSON.stringify(usuario));
        this.authService.setUsuario(usuario);
        this.router.navigate(['/']);
      } else {
        this.error = 'Email o contraseña incorrectos';
      }
    } catch (error: any) {
      if (error?.error?.message?.includes('verificada')) {
        this.error = 'Tu cuenta aún no está verificada. Revisa tu correo electrónico.';
      } else {
        this.error = 'Email o contraseña incorrectos.';
      }
    }
  }

  loginWithGoogle() {
    // Implementar login con Google OAuth
  }

  loginWithFacebook() {
    // Implementar login con Facebook OAuth
  }
}
