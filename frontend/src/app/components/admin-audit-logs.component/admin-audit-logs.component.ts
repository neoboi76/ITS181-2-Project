import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditLogService } from '../../services/audit-log.service';
import { AuditLog } from '../../models/audit-log.model';

@Component({
    selector: 'app-admin-audit-logs',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-audit-logs.component.html',
    styleUrl: './admin-audit-logs.component.css'
})
export class AdminAuditLogsComponent implements OnInit {
    auditLogs: AuditLog[] = [];
    filteredLogs: AuditLog[] = [];
    isLoading = true;
    searchTerm = '';
    filterAction = '';
    filterSuccess = '';
    selectedLog: AuditLog | null = null;
    showDetailModal = false;
    errorMessage = '';

    uniqueActions: string[] = [];

    constructor(private auditLogService: AuditLogService) {}

    ngOnInit(): void {
        window.scrollTo(0, 0);
        this.loadAuditLogs();
    }

    loadAuditLogs(): void {
        this.auditLogService.getAllAuditLogs().subscribe({
            next: (logs) => {
                this.auditLogs = logs.sort((a, b) => 
                    new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
                );
                this.filteredLogs = this.auditLogs;
                this.extractUniqueActions();
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error loading audit logs:', err);
                this.errorMessage = 'Failed to load audit logs';
                this.isLoading = false;
            }
        });
    }

    extractUniqueActions(): void {
        const actions = new Set(this.auditLogs.map(log => log.action));
        this.uniqueActions = Array.from(actions).sort();
    }

    applyFilters(): void {
        this.filteredLogs = this.auditLogs.filter(log => {
            const matchesSearch = !this.searchTerm || 
                log.details?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                log.ipAddress?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                log.userAgent?.toLowerCase().includes(this.searchTerm.toLowerCase());
            
            const matchesAction = !this.filterAction || log.action === this.filterAction;
            
            const matchesSuccess = !this.filterSuccess || 
                (this.filterSuccess === 'true' && log.success) ||
                (this.filterSuccess === 'false' && !log.success);

            return matchesSearch && matchesAction && matchesSuccess;
        });
    }

    clearFilters(): void {
        this.searchTerm = '';
        this.filterAction = '';
        this.filterSuccess = '';
        this.filteredLogs = this.auditLogs;
    }

    viewDetails(log: AuditLog): void {
        this.selectedLog = log;
        this.showDetailModal = true;
        document.body.style.overflow = 'hidden';
    }

    closeDetailModal(): void {
        this.showDetailModal = false;
        this.selectedLog = null;
        document.body.style.overflow = 'auto';
    }

    getActionColor(action: string): string {
        const actionLower = action.toLowerCase();
        if (actionLower.includes('login') || actionLower.includes('register')) {
            return 'bg-blue-100 text-blue-800';
        } else if (actionLower.includes('created') || actionLower.includes('updated')) {
            return 'bg-green-100 text-green-800';
        } else if (actionLower.includes('deleted') || actionLower.includes('failed')) {
            return 'bg-red-100 text-red-800';
        } else if (actionLower.includes('suspended')) {
            return 'bg-orange-100 text-orange-800';
        } else if (actionLower.includes('viewed')) {
            return 'bg-purple-100 text-purple-800';
        }
        return 'bg-gray-100 text-gray-800';
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    formatAction(action: string): string {
        return action.replace(/_/g, ' ').toLowerCase()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    }

    exportLogs(): void {
        const csvContent = this.generateCSV();
        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `audit-logs-${new Date().toISOString().split('T')[0]}.csv`;
        link.click();
        window.URL.revokeObjectURL(url);
    }

    generateCSV(): string {
        const headers = ['Timestamp', 'Action', 'User', 'IP Address', 'Success', 'Details'];
        const rows = this.filteredLogs.map(log => [
            this.formatDate(log.timestamp),
            log.action,
            log.user ? `${log.user.firstName} ${log.user.lastName}` : 'System',
            log.ipAddress,
            log.success ? 'Yes' : 'No',
            log.details || ''
        ]);

        const csvRows = [
            headers.join(','),
            ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
        ];

        return csvRows.join('\n');
    }
}