import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/audit-log.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({
    providedIn: 'root'
})
export class AuditLogService {
    private apiUrl = 'http://localhost:8080/admin/audit-logs';

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

    getAllAuditLogs(): Observable<AuditLog[]> {
        return this.http.get<AuditLog[]>(this.apiUrl, { headers: this.getHeaders() });
    }
}