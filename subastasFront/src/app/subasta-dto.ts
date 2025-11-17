export interface SubastaDTO {
    
    title: string;
    description: string;
    categoria: string;
    precioInicial: number;
    fechaInicio: string;
    fechaFin: string;
    ubicacion: string;
    imagenes: string[];
    estado: string | null;
    martilleroId: number | null;
    userId: number;
    userGanadorId: number | null;
    incrementoFijo: number;
    emailCreador: string;
}
