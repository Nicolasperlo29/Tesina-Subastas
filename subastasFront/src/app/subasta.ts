import { Categoria } from "./categoria";

export interface Subasta {

    id: number;
    title: string;
    description: string;
    categoria: string;
    precioInicial: number;
    fechaInicio: string;
    ubicacion: string;
    fechaFin: string;
    martilleroId: number | null;
    imagenes: string[];
    estado: string;
    userId: number;
    userGanadorId: number | null;
    incrementoFijo: number;
}
