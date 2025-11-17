import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../user';
import { UsuariosService } from '../../services/usuarios.service';
import { CommonModule } from '@angular/common';
import { UserDTOPost } from '../../user-dtopost';
import { RecaptchaModule } from 'ng-recaptcha';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RecaptchaModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  submitted = false;

  captcha: string | null = null;
  captchaError = false;

  isAdmin?: boolean;
  usuario: User | null = null

  isModalOpen: boolean = false;
  errorMessage: string = '';

  buttonActivar: boolean = false;

  constructor(private service: UsuariosService, private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-ZÀ-ÿ\s]+$/)]],
      lastname: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-ZÀ-ÿ\s]+$/)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      acceptTerms: [false, [Validators.requiredTrue]],
      rol: ['']
    }
    );
  }

  get f() { return this.registerForm.controls; }

  ngOnInit(): void {
    this.authService.usuarioActual$.subscribe(usuario => {
      this.usuario = usuario;

      if (usuario) {
        this.isAdmin = usuario.rol === 'admin';
        console.log('Usuario:', usuario);
        console.log('Rol:', usuario.rol);
      }
    });
  }

  onCaptchaResolved(captchaResponse: string | null) {
    this.captcha = captchaResponse;
    this.captchaError = false;
  }

  onSubmit() {
    this.submitted = true;

    if (this.registerForm.valid) {

      const { ...formData } = this.registerForm.value;

      const user: UserDTOPost = {
        ...formData,
        captchaResponse: this.captcha
      };

      if (!this.isAdmin) {
        user.rol = 'user';
      }

      this.service.crearUsuario(user).subscribe({
        next: (response) => {
          console.log('Usuario creado:', response);
          this.isModalOpen = true;
        },
        error: (error) => {
          const errorCode = error.error?.errorCode;
          const message = error.error?.message || 'Error inesperado';

          switch (errorCode) {
            case 'USER_INACTIVE':
              this.errorMessage = message;
              this.buttonActivar = true;
              break;

            case 'USER_EXISTS':
              this.errorMessage = message;
              // alert(this.errorMessage);
              break;

            case 'NOTIF_FAILED':
              alert('No se pudo enviar el correo de verificación. Intentalo más tarde.');
              this.registerForm.reset();
              this.captcha = null;
              break;

            default:
              alert('Revisa tu email.');
              this.registerForm.reset();
              this.captcha = null;
          }
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
      if (!this.captcha) alert("Verificá el captcha.");
    }
  }

  cerrarModal() {
    this.router.navigate(['/login'])
    this.registerForm.reset();
  }

  activarCuenta() {
    this.service.activarCuenta(this.registerForm.value.email).subscribe({
      next: (response) => {
        console.log('Usuario activado:', response);
      },
      error: (error) => {
        console.log("Error: ", error)
      }
    })
  }
}
