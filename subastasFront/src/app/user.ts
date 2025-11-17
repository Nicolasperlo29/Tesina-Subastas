export interface User {

    id: number;
    name: string;
    lastname: string;
    username: string;
    email: string;
    password: string;
    rol: string;
    numberphone: string;
    verificationToken: string;
    verified: boolean;
    activo: boolean;
    fechaBaja: string;
}