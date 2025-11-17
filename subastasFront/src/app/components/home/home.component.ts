import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SubastaDTO } from '../../subasta-dto';
import { SubastasService } from '../../services/subastas.service';
import { Subasta } from '../../subasta';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule, DatePipe, CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  logeado: boolean = false;
  subastas: Subasta[] = [];

  constructor(private service: AuthService, private subastasService: SubastasService) { }

  ngOnInit(): void {
    let index = 0;
    const images = document.querySelectorAll('.hero-image');
    setInterval(() => {
      images.forEach(img => img.classList.remove('active'));
      index = (index + 1) % images.length;
      images[index].classList.add('active');
    }, 6500); // cambia cada 6.5 segundos
    this.service.usuarioActual$.subscribe(usuario => {
      this.logeado = usuario !== null;
    });
    this.subastasService.getAllSubastas().subscribe(subastas => {
      this.subastas = subastas.slice(0, 3);
    })
  }
}