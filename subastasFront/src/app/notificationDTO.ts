export interface NotificationDTO {

    id: number;
    
    destinatario: string;

    userId: number;

    asunto: string;

    cuerpo: string;

    fechaEnvio: string;
}
