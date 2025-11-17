import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { VenderComponent } from './components/vender/vender.component';
import { MisSubastasComponent } from './components/mis-subastas/mis-subastas.component'; 
import { SubastaComponent } from './components/VistaSubasta/subasta/subasta.component';
import { AllSubastasComponent } from './components/all-subastas/all-subastas.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { NotificationsComponent } from './components/notifications/notifications.component'; 
import { ProfileGuard } from './profile';
import { authGuard } from './auth.guard';
import { loginGuard } from './login.guard';
import { RecuperarContrasenaComponent } from './components/recuperar-contrasena/recuperar-contrasena.component'; 
import { ResetPasswordComponent } from './components/reset-password/reset-password.component'; 
import { TerminosComponent } from './components/terminos/terminos.component';
import { AyudaComponent } from './components/ayuda/ayuda.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent, canActivate: [loginGuard] },
    { path: 'misSubastas', component: MisSubastasComponent },
    { path: 'vender', component: VenderComponent },
    { path: 'subasta/:id', component: SubastaComponent },
    { path: 'subastas/:categoria', component: AllSubastasComponent },
    { path: 'passwordRecovery', component: RecuperarContrasenaComponent },
    { path: 'terminos', component: TerminosComponent },
    { path: 'ayuda', component: AyudaComponent },
    { path: 'restablecer-password', component: ResetPasswordComponent },
    { path: 'notifications', component: NotificationsComponent },
    { path: 'subastas', component: AllSubastasComponent },
    { path: 'profile', component: ProfileComponent, canActivate: [ProfileGuard] },
    { path: 'register', component: RegisterComponent },
    { path: 'admin', component: AdminDashboardComponent , canActivate: [authGuard] }
];
