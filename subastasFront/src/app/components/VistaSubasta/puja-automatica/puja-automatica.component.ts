import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PujaAutomaticaDTOPost } from '../../../puja-automatica-dtopost'; 
import { SubastasService } from '../../../services/subastas.service'; 
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-puja-automatica',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './puja-automatica.component.html',
  styleUrl: './puja-automatica.component.css'
})
export class PujaAutomaticaComponent implements OnInit {

  @Input() isModalPujaOpen: boolean = false;
  @Output() cerrar = new EventEmitter<void>();
  @Input() subastaId!: number;
  @Input() userId!: number;
  @Input() realizoElDeposito: boolean = false;

  valorMaximoInput: number = 0;

  constructor(private subastaService: SubastasService) {}

  ngOnInit(): void {
    
  }
  
  confirmar() {
    const puja: PujaAutomaticaDTOPost = {
      valorMaximo: this.valorMaximoInput,
      subastaId: this.subastaId,
      userId: this.userId
    };

    console.log('Usuario id: ', this.userId)
    console.log(this.subastaId)
    if (puja.userId === this.subastaId) {
      alert('No puedes pujar en tus subastas.');
      return;
    }

    if (this.realizoElDeposito == false) {
      alert('Primero debes ralizar el deposito de garantia');
      return
    }

    this.subastaService.crearPujaAutomatica(puja).subscribe({
        next: (response) => {
          console.log('Creada: ', response)
          this.cerrar.emit();
        },
        error: (err) => console.error('Error creando la puja automatica: ', err)
    })
  }

  cerrarModal() {
    this.cerrar.emit();
  }
}
