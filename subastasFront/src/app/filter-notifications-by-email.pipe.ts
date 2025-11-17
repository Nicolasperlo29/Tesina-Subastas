import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filterNotificationsByEmail',
  standalone: true
})
export class FilterNotificationsByEmailPipe implements PipeTransform {

  transform(notifications: any[], email: string, fecha: string): any[] {
    if (!notifications) return [];

    return notifications.filter(notification => {
      const coincideEmail = email
        ? notification.destinatario?.toLowerCase().includes(email.toLowerCase())
        : true;

      const coincideFecha = fecha
        ? notification.fechaEnvio?.toLowerCase().includes(fecha.toLowerCase())
        : true;

      return coincideEmail && coincideFecha;
    });
  }

}
