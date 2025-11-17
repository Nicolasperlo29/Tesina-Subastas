export interface Pago {

    id: number;
    monto: number;
    fechaConfirmacion: string;
    descripcion: string;
    usuarioId: number;
    subastaId: number;
    emailUsuario: string;
}
