import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicationService } from '../../services/application.service';
import { ApplicationForm } from '../../models/application.model';

@Component({
  selector: 'app-admin-applications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-applications.component.html',
  styleUrl: './admin-applications.component.css'
})
export class AdminApplicationsComponent implements OnInit {
  applications: ApplicationForm[] = [];
  filteredApplications: ApplicationForm[] = [];
  isLoading = true;
  selectedApplication: ApplicationForm | null = null;
  showDetailModal = false;
  filterStatus = '';
  searchTerm = '';
  successMessage = '';
  errorMessage = '';

  constructor(private applicationService: ApplicationService) {}

  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications(): void {
    this.applicationService.getAllApplications().subscribe({
      next: (applications) => {
        this.applications = applications.sort((a, b) => 
          new Date(b.submittedAt!).getTime() - new Date(a.submittedAt!).getTime()
        );
        this.filteredApplications = this.applications;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading applications:', err);
        this.errorMessage = 'Failed to load applications';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredApplications = this.applications.filter(app => {
      const matchesSearch = !this.searchTerm || 
        `${app.firstName} ${app.lastName}`.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        app.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = !this.filterStatus || app.status === this.filterStatus;

      return matchesSearch && matchesStatus;
    });
  }

  viewDetails(application: ApplicationForm): void {
    this.selectedApplication = application;
    this.showDetailModal = true;
    document.body.style.overflow = 'hidden';
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedApplication = null;
    document.body.style.overflow = 'auto';
  }

  updateStatus(applicationId: number | undefined, newStatus: string): void {
    if (!applicationId) return;

    this.applicationService.updateApplicationStatus(applicationId, newStatus).subscribe({
      next: (updatedApp) => {
        const index = this.applications.findIndex(a => a.applicationId === applicationId);
        if (index !== -1) {
          this.applications[index] = updatedApp;
        }
        this.applyFilters();
        this.successMessage = `Application status updated to ${newStatus}`;
        setTimeout(() => this.successMessage = '', 3000);
        
        if (this.selectedApplication?.applicationId === applicationId) {
          this.selectedApplication = updatedApp;
        }
        this.closeDetailModal();
      },
      error: (err) => {
        console.error('Error updating status:', err);
        this.errorMessage = 'Failed to update application status';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  getStatusColor(status: string | undefined): string {
    switch(status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED': return 'bg-green-100 text-green-800';
      case 'REJECTED': return 'bg-red-100 text-red-800';
      case 'COMPLETED': return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}