import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DogService } from '../../services/dog.service';
import { ApplicationService } from '../../services/application.service';
import { UserManagementService } from '../../services/user-management.service';
import { Dog } from '../../models/dog.model';
import { ApplicationForm } from '../../models/application.model';

@Component({
    selector: 'app-admin-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './admin-dashboard.component.html',
    styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
    stats = {
        totalDogs: 0,
        availableDogs: 0,
        adoptedDogs: 0,
        totalUsers: 0,
        pendingApplications: 0
    };

    recentDogs: Dog[] = [];
    recentApplications: ApplicationForm[] = [];
    isLoading = true;

    constructor(
        private dogService: DogService,
        private applicationService: ApplicationService,
        private userService: UserManagementService
    ) {}

    ngOnInit(): void {
        this.loadDashboardData();
    }

    loadDashboardData(): void {
        this.dogService.getAllDogs().subscribe({
            next: (dogs) => {
                this.stats.totalDogs = dogs.length;
                this.stats.availableDogs = dogs.filter(d => d.status === 'AVAILABLE').length;
                this.stats.adoptedDogs = dogs.filter(d => d.status === 'ADOPTED').length;
                this.recentDogs = dogs
                    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                    .slice(0, 5);
            },
            error: (err) => console.error('Error loading dogs:', err)
        });

        this.applicationService.getAllApplications().subscribe({
            next: (applications) => {
                this.stats.pendingApplications = applications.filter(a => a.status === 'PENDING').length;
                this.recentApplications = applications
                    .sort((a, b) => new Date(b.submittedAt!).getTime() - new Date(a.submittedAt!).getTime())
                    .slice(0, 5);
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error loading applications:', err);
                this.isLoading = false;
            }
        });

        this.userService.getAllUsers().subscribe({
            next: (users) => {
                this.stats.totalUsers = users.length;
            },
            error: (err) => console.error('Error loading users:', err)
        });
    }

    getStatusColor(status: string): string {
        switch(status) {
            case 'AVAILABLE': return 'text-green-600 bg-green-100';
            case 'PENDING': return 'text-yellow-600 bg-yellow-100';
            case 'ADOPTED': return 'text-blue-600 bg-blue-100';
            case 'APPROVED': return 'text-green-600 bg-green-100';
            case 'REJECTED': return 'text-red-600 bg-red-100';
            default: return 'text-gray-600 bg-gray-100';
        }
    }
}