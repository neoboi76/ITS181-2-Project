import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationForm } from '../models/application.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = 'http://localhost:8080/applications';

  constructor(
    private http: HttpClient,
    private tokenStorageService: TokenStorageService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.tokenStorageService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  createApplication(application: ApplicationForm): Observable<ApplicationForm> {
    return this.http.post<ApplicationForm>(this.apiUrl, application, 
      { headers: this.getHeaders() });
  }

  getAllApplications(): Observable<ApplicationForm[]> {
    return this.http.get<ApplicationForm[]>(this.apiUrl, 
      { headers: this.getHeaders() });
  }


  updateApplicationStatus(id: number, status: string): Observable<ApplicationForm> {
    const params = new HttpParams().set('status', status);
    return this.http.put<ApplicationForm>(
      `${this.apiUrl}/${id}/status`, 
      null, 
      { headers: this.getHeaders(), params }
    );
  }
}