import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContactForm } from '../models/contact.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private apiUrl = 'http://localhost:8080/contact';

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

  submitContactForm(contact: ContactForm): Observable<ContactForm> {
    return this.http.post<ContactForm>(this.apiUrl, contact, 
        { headers: this.getHeaders() }
    );
  }

  getAllContactForms(): Observable<ContactForm[]> {
    return this.http.get<ContactForm[]>(this.apiUrl, { headers: this.getHeaders() });
  }
}