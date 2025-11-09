import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dog } from '../models/dog.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class DogService {
  private apiUrl = 'http://localhost:8080/dogs';

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

  getAllDogs(): Observable<Dog[]> {
    return this.http.get<Dog[]>(this.apiUrl,
    );
  }

  getDogById(id: number): Observable<Dog> {
    return this.http.get<Dog>(`${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  getAvailableDogs(): Observable<Dog[]> {
    return this.http.get<Dog[]>(`${this.apiUrl}/status/AVAILABLE`,
    );
  }

  createDog(dog: Dog): Observable<Dog> {
    return this.http.post<Dog>(this.apiUrl, dog, 
      { headers: this.getHeaders() });
  }

  updateDog(id: number, dog: Dog): Observable<Dog> {
    return this.http.put<Dog>(`${this.apiUrl}/${id}`, dog, 
      { headers: this.getHeaders() });
  }

  deleteDog(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, 
      { headers: this.getHeaders() });
  }
}
