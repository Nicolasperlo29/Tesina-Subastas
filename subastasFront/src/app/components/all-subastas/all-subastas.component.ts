import { Component, OnInit } from '@angular/core';
import { Subasta } from '../../subasta';
import { SubastasService } from '../../services/subastas.service';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-all-subastas',
  standalone: true,
  imports: [RouterModule, DatePipe, CommonModule, FormsModule],
  templateUrl: './all-subastas.component.html',
  styleUrl: './all-subastas.component.css'
})
export class AllSubastasComponent implements OnInit {

  subastas: Subasta[] = [];
  subastasFiltradas: Subasta[] = [];
  categoria: string = '';
  estadoSeleccionado: string = "ACTIVA";
  title: string = '';
  isLoading: boolean = false;

  // Filtros adicionales
  busqueda: string = '';
  precioMin: number | null = null;
  precioMax: number | null = null;
  ordenamiento: string = 'fecha-desc'; // fecha-desc, fecha-asc, precio-asc, precio-desc

  // Paginación
  currentPage: number = 1;
  itemsPerPage: number = 9;

  constructor(private serviceSubastas: SubastasService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.categoria = params['categoria'];
      this.cargarSubastas();
    });
    if (this.categoria == null) {
      this.title = "Todas las subastas"
    }
  }

  cargarSubastas() {
    this.isLoading = true;

    if (this.categoria) {
      this.serviceSubastas.getSubastasCategoriaEstado(this.categoria, this.estadoSeleccionado).subscribe({
        next: (response) => {
          this.subastas = response;
          this.aplicarFiltros();
          this.isLoading = false;
        },
        error: (error) => {
          console.error(error);
          this.isLoading = false;
        }
      });
    } else {
      this.serviceSubastas.getSubastasPorEstado(this.estadoSeleccionado).subscribe({
        next: (response) => {
          this.subastas = response;
          this.aplicarFiltros();
          this.isLoading = false;
        },
        error: (error) => {
          console.error(error);
          this.isLoading = false;
        }
      });
    }
  }

  aplicarFiltros() {
    let resultado = [...this.subastas];

    // Filtro por búsqueda de texto
    if (this.busqueda.trim()) {
      const busquedaLower = this.busqueda.toLowerCase();
      resultado = resultado.filter(subasta =>
        subasta.title.toLowerCase().includes(busquedaLower) ||
        subasta.ubicacion?.toLowerCase().includes(busquedaLower)
      );
    }

    // Filtro por rango de precio
    if (this.precioMin !== null && this.precioMin > 0) {
      resultado = resultado.filter(subasta => subasta.precioInicial >= this.precioMin!);
    }
    if (this.precioMax !== null && this.precioMax > 0) {
      resultado = resultado.filter(subasta => subasta.precioInicial <= this.precioMax!);
    }

    // Ordenamiento
    resultado = this.ordenarSubastas(resultado);

    this.subastasFiltradas = resultado;
    this.currentPage = 1; // Reset a primera página al filtrar
  }

  ordenarSubastas(subastas: Subasta[]): Subasta[] {
    const resultado = [...subastas];

    switch (this.ordenamiento) {
      case 'fecha-desc':
        return resultado.sort((a, b) =>
          new Date(b.fechaFin).getTime() - new Date(a.fechaFin).getTime()
        );
      case 'fecha-asc':
        return resultado.sort((a, b) =>
          new Date(a.fechaFin).getTime() - new Date(b.fechaFin).getTime()
        );
      case 'precio-asc':
        return resultado.sort((a, b) => a.precioInicial - b.precioInicial);
      case 'precio-desc':
        return resultado.sort((a, b) => b.precioInicial - a.precioInicial);
      default:
        return resultado;
    }
  }

  get subastasPaginadas() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.subastasFiltradas.slice(start, start + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.subastasFiltradas.length / this.itemsPerPage);
  }

  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPages) {
      this.currentPage = nuevaPagina;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  activarFiltroEstado(estado: string): void {
    this.estadoSeleccionado = estado;
    this.cargarSubastas();
  }

  min(a: number, b: number): number {
    return Math.min(a, b);
  }

  limpiarFiltros() {
    this.busqueda = '';
    this.precioMin = null;
    this.precioMax = null;
    this.ordenamiento = 'fecha-desc';
    this.aplicarFiltros();
  }

  onBusquedaChange() {
    this.aplicarFiltros();
  }

  onPrecioChange() {
    this.aplicarFiltros();
  }

  onOrdenamientoChange() {
    this.aplicarFiltros();
  }

  get paginasVisibles(): number[] {
    const total = this.totalPages;
    const current = this.currentPage;
    const paginas: number[] = [];

    if (total <= 7) {
      for (let i = 1; i <= total; i++) {
        paginas.push(i);
      }
    } else {
      if (current <= 3) {
        paginas.push(1, 2, 3, 4, -1, total);
      } else if (current >= total - 2) {
        paginas.push(1, -1, total - 3, total - 2, total - 1, total);
      } else {
        paginas.push(1, -1, current - 1, current, current + 1, -1, total);
      }
    }

    return paginas;
  }
}