import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subasta } from '../../subasta';
import { Categoria } from '../../categoria';
import { SubastasService } from '../../services/subastas.service';
import { AuthService } from '../../services/auth.service';
import { SubastaDTO } from '../../subasta-dto';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UbicacionService } from '../../services/ubicacion.service';
import { UsuariosService } from '../../services/usuarios.service';

@Component({
  selector: 'app-vender',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './vender.component.html',
  styleUrl: './vender.component.css'
})
export class VenderComponent implements OnInit {

  formSubasta!: FormGroup;

  categoria: boolean = false;
  imagenes: string[] = [];
  categoriaSeleccionada: string = '';
  subasta?: SubastaDTO;
  isModalErrorOpen: boolean = false;
  error: string = '';
  minDateTime: string = '';

  userId: number = 0;
  email: string = "";

  pais: string = 'Argentina';
  ciudades: string[] = [];

  isModalOpen = false;
  imagenesError = false;

  ngOnInit(): void {
    const ahora = new Date();

    this.minDateTime = new Date(ahora.getTime() - ahora.getTimezoneOffset() * 60000)
      .toISOString()
      .slice(0, 16);

    const user = this.authService.getUsuario();

    if (user) {
      this.userId = user.id;
      this.email = user.email;
      console.log('usuario: ', user.id)
    } else {
      console.error('No hay usuario logueado');
    }

    this.ubicacionService.getCiudades(this.pais).subscribe(response => {
      this.ciudades = response.data;
    });

    this.formSubasta = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(40)]],
      description: ['', [Validators.required, Validators.maxLength(250)]],
      precioInicial: [0, [Validators.required, Validators.min(10000), Validators.max(999999999)]],
      imagen: [''],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      ubicacion: [''],
      incrementoFijo: [0, [Validators.min(1000), Validators.max(1000000)]]
    });
  }

  constructor(
    private fb: FormBuilder,
    private service: SubastasService,
    private authService: AuthService,
    private router: Router,
    private ubicacionService: UbicacionService,
    private userService: UsuariosService
  ) { }


  confirmarCategoria(nombre: string) {
    this.categoria = true;
    this.categoriaSeleccionada = nombre;
  }

  agregarImagen() {
    const url = this.formSubasta.get('imagen')?.value;
    if (url && this.imagenes.length <= 10) {
      this.imagenes.push(url);
      this.formSubasta.get('imagen')?.reset();
    }
    this.imagenesError = this.imagenes.length < 3;
  }

  onSubmit() {

    console.log('Click')
    if (this.formSubasta.invalid) {
      this.formSubasta.markAllAsTouched();
      return;
    }

    if (this.imagenes.length < 3) {
      this.imagenesError = true;
      return;
    }

    const form = this.formSubasta.value;

    if (new Date(form.fechaFin) <= new Date(form.fechaInicio)) {
      this.abrirModal('La fecha de fin debe ser posterior a la fecha de inicio.');
      return;
    }

    const fechaInicio = new Date(form.fechaInicio);
    const fechaFin = new Date(form.fechaFin);
    const ahora = new Date();
    const fechaInicioMas10Min = new Date(fechaInicio.getTime() + 10 * 60 * 1000);

    if (fechaInicioMas10Min < ahora) {
      this.abrirModal('La fecha de inicio no puede ser anterior al momento actual.');
      return;
    }

    const fechaInicioMas1Hora = new Date(fechaInicio.getTime() + 60 * 60 * 1000);
    if (fechaFin < fechaInicioMas1Hora) {
      this.abrirModal('La fecha de fin debe ser al menos una hora después de la fecha de inicio.');
      return;
    }

    this.subasta = {
      title: form.title,
      description: form.description,
      precioInicial: form.precioInicial,
      fechaInicio: new Date(form.fechaInicio).toISOString(),
      fechaFin: new Date(form.fechaFin).toISOString(),
      categoria: this.categoriaSeleccionada,
      ubicacion: form.ubicacion,
      imagenes: this.imagenes,
      estado: null,
      martilleroId: null,
      userId: this.userId,
      userGanadorId: null,
      incrementoFijo: form.incrementoFijo,
      emailCreador: this.email
    };

    if (this.categoriaSeleccionada === 'Vehiculos' || this.subasta.precioInicial >= 7000000) {
      this.subasta.martilleroId = 35;
    }

    if (!this.userId) {
      alert('Debes estar logeado');
      return;
    }

    this.service.crearSubasta(this.subasta).subscribe({
      next: (response) => {
        this.isModalOpen = true;
      },
      error: (err) => console.error(err)
    });
  }

  eliminarImagen(index: number) {
    this.imagenes.splice(index, 1);
    this.imagenesError = this.imagenes.length < 3;
  }

  volver() {
    this.router.navigate(['/subastas']);
  }
  // notificarSubasta() {
  //   this.userService.notificarUsuarioGanador(this.email,).subscribe({
  //     next: (respuesta) => console.log('Notificación enviada:', respuesta),
  //     error: (err) => console.error('Error al enviar la notificación:', err)
  //   });
  // }

  abrirModal(error: string) {
    this.isModalErrorOpen = true;
    this.error = error;
  }

  cerrarModal() {
    this.isModalOpen = false;
    this.router.navigate(['/subastas'])
  }
}
