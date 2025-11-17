import { Component, OnInit } from '@angular/core';
import { Subasta } from '../../subasta';
import { SubastasService } from '../../services/subastas.service';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-mis-subastas',
  standalone: true,
  imports: [RouterModule, FormsModule, ReactiveFormsModule, CommonModule, CurrencyPipe, DatePipe],
  templateUrl: './mis-subastas.component.html',
  styleUrl: './mis-subastas.component.css'
})
export class MisSubastasComponent implements OnInit{

  subastas: Subasta[] = [];
  userId: number = 0;
  isMartillero: boolean = false;
  isModalOpen: boolean = false;
  pujas: boolean = false;

  formulario!: FormGroup;
  subastaSeleccionadaId: number = 0;

  constructor(private serviceSubastas: SubastasService, private authService: AuthService, private fb: FormBuilder) {}

  ngOnInit(): void {
    const user = this.authService.getUsuario();

    if (user) {
      this.userId = user.id;
      console.log('usuario: ', user.id)
    } else {
      console.error('No hay usuario logueado');
    }

    this.authService.usuarioActual$.subscribe(usuario => {
      if (usuario) {
        this.isMartillero = usuario.rol === 'martillero';

        if (this.isMartillero) {
          this.cargarSubastasMartillero(this.userId);
        }
        else {
          this.cargarSubastas(this.userId)
        }
      }
    });

      this.formulario = this.fb.group({
        title: [''],
        monto: [{value: 0, disabled: true}],
        description: [''],
        ubicacion: ['']
      });

    console.log(this.isMartillero)
    console.log(this.userId)
    
  }

  imagenesActuales: string[] = [];
  imagen: string = '';
  nuevasImagenesUrls: string[] = [];

  cargarSubastasMartillero(userId: number) {
    this.serviceSubastas.getSubastasMartilleroId(userId).subscribe({
      next: (response) => {
        this.subastas = response;
        console.log('Subastass', response)
      },
      error: (error) => {
        console.error('Error al traer las subastas:', error)
      }
    })
  }

  cargarSubastas(userId: number) {
    this.serviceSubastas.getSubastasIdUsuario(userId).subscribe({
      next: (response) => {
        this.subastas = response;
        console.log('Subastas', response)
      },
      error: (error) => {
        console.error('Error al traer las subastas:', error)
      }
    })
  }

  modificar(id: number) {
    this.isModalOpen = true;
    this.serviceSubastas.getPujasSubastaId(id).subscribe({
      next: (response) => {
        if (response.length > 0) {
          this.formulario.get('monto')?.disable();
        }
      },
      error: (error) => {
        console.error('Error al traer las pujas:', error)
      }
    })

    const subasta = this.subastas.find(s => s.id === id);
    if (!subasta) return;

    this.subastaSeleccionadaId = id;

    this.formulario.patchValue({
      title: subasta.title,
      monto: subasta.precioInicial,
      description: subasta.description,
      ubicacion: subasta.ubicacion
    });

    this.imagenesActuales = [...subasta.imagenes];

    this.isModalOpen = true;
    console.log(this.imagenesActuales)
  }

  agregarImagen() {
    if (this.imagen && this.imagenesActuales.length <= 10) {
      this.imagenesActuales.push(this.imagen);
      this.imagen = '';
    }
  }

  eliminarImagen(index: number) {
    this.imagenesActuales.splice(index, 1);
    console.log(this.imagenesActuales)
  }

  guardarCambios() {
    if (this.formulario.invalid) return;

    const imagenesActualizadas = [...this.imagenesActuales, ...this.nuevasImagenesUrls];

    const dto = {
      ...this.formulario.getRawValue(),
      nuevasImagenes: imagenesActualizadas
    };

    this.serviceSubastas.actualizarSubasta(this.subastaSeleccionadaId, dto)
      .subscribe({
        next: () => {
          this.cerrarModal();
          this.ngOnInit();
          this.nuevasImagenesUrls = [];
          this.imagenesActuales = [];
        },
        error: err => console.error('Error actualizando subasta', err)
      });
  }

  eliminarSubasta(subastaId: number) {
    this.serviceSubastas.eliminarSubasta(subastaId).subscribe({
      next: (res) => {
        console.log('Eliminada: ', res)
        this.cargarSubastas(this.userId);
      },
      error: (err) => {
        console.log('Error: ', err)
        alert('No se pueden eliminar subastas con pujas')
      }
    })
  }

  cerrarModal() {
    this.isModalOpen = false;
  }
}
