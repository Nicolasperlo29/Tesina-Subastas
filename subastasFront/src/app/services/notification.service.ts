import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { NotificationDTO } from '../notificationDTO';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private http: HttpClient) { }

  private url = "http://localhost:8087/notification"

  getNotificationsUserId(userId: number): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`http://localhost:8087/notification/${userId}`);
  }

  ocultarNotificacion(id: number): Observable<void> {
    return this.http.put<void>(`${this.url}/${id}/ocultar`, {});
  }

  getNotifications(): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>("http://localhost:8087/notification/all");
  }
}
