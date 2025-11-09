import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { UserResponse } from '../models/admin-user-response.model';

@Injectable({
    providedIn: 'root'
})
export class UserManagementService {
    private apiUrl = 'http://localhost:8080/admin/users';

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

    getAllUsers(): Observable<UserResponse[]> {
        return this.http.get<UserResponse[]>(this.apiUrl, 
            { headers: this.getHeaders() });
    }

    suspendUser(userId: number): Observable<UserResponse> {
        return this.http.put<UserResponse>(
            `${this.apiUrl}/${userId}/suspend`, 
            null,
            { headers: this.getHeaders() }
        );
    }

    activateUser(userId: number): Observable<UserResponse> {
        return this.http.put<UserResponse>(
            `${this.apiUrl}/${userId}/activate`, 
            null,
            { headers: this.getHeaders() }
        );
    }

    deleteUser(userId: number): Observable<void> {
        return this.http.delete<void>(
            `${this.apiUrl}/${userId}`, 
            { headers: this.getHeaders() }
        );
    }

    promoteUser(userId: number): Observable<UserResponse> {
        return this.http.put<UserResponse>(
            `${this.apiUrl}/${userId}/promote`, 
            null,
            { headers: this.getHeaders() }
        );
    }
}