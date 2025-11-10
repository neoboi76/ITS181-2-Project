import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserManagementService } from '../../services/user-management.service';
import { User } from '../../models/admin-user.model';

interface UserWithDropdown extends User {
  showDropdown: boolean;
  dropdownPosition?: { top: number; left: number };
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
  users: UserWithDropdown[] = [];
  filteredUsers: UserWithDropdown[] = [];
  isLoading = true;
  searchTerm = '';
  filterRole = '';
  filterStatus = '';
  successMessage = '';
  errorMessage = '';
  showConfirmModal = false;
  confirmAction: 'suspend' | 'activate' | 'delete' | 'promote' | null = null;
  selectedUser: UserWithDropdown | null = null;

  constructor(private userService: UserManagementService) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users
          .map(u => ({ ...u, showDropdown: false }))
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.filteredUsers = this.users;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.errorMessage = 'Failed to load users';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch =
        !this.searchTerm ||
        `${user.firstName} ${user.lastName}`.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesRole = !this.filterRole || user.role === this.filterRole;
      const matchesStatus =
        !this.filterStatus ||
        (this.filterStatus === 'active' && !user.suspended) ||
        (this.filterStatus === 'suspended' && user.suspended);
      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filterRole = '';
    this.filterStatus = '';
    this.filteredUsers = this.users;
  }

  toggleDropdown(user: UserWithDropdown, event: MouseEvent): void {
    event.stopPropagation();
    const willOpen = !user.showDropdown;
    this.users.forEach(u => u.showDropdown = false);
    if (!willOpen) {
      user.showDropdown = false;
      return;
    }
    const button = (event.currentTarget as HTMLElement);
    const rect = button.getBoundingClientRect();
    const panelWidth = 176;
    let left = Math.round(rect.left);
    const rightOverflow = left + panelWidth > window.innerWidth - 8;
    if (rightOverflow) left = Math.max(8, window.innerWidth - panelWidth - 8);
    const top = Math.round(rect.bottom + 6);
    user.dropdownPosition = { top, left };
    user.showDropdown = true;
  }

  onActionSelect(action: 'suspend' | 'activate' | 'delete' | 'promote', user: UserWithDropdown): void {
    user.showDropdown = false;
    this.openConfirmModal(action, user);
  }

  openConfirmModal(action: 'suspend' | 'activate' | 'delete' | 'promote', user: UserWithDropdown): void {
    this.confirmAction = action;
    this.selectedUser = user;
    this.showConfirmModal = true;
    document.body.style.overflow = 'hidden';
  }

  closeConfirmModal(): void {
    this.showConfirmModal = false;
    this.confirmAction = null;
    this.selectedUser = null;
    document.body.style.overflow = 'auto';
  }

  confirmActionExecute(): void {
    if (!this.selectedUser || !this.confirmAction) return;
    switch (this.confirmAction) {
      case 'suspend':
        this.suspendUser(this.selectedUser.userId);
        break;
      case 'activate':
        this.activateUser(this.selectedUser.userId);
        break;
      case 'delete':
        this.deleteUser(this.selectedUser.userId);
        break;
      case 'promote':
        this.promoteUser(this.selectedUser.userId);
        break;
    }
  }

  suspendUser(userId: number): void {
    this.userService.suspendUser(userId).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.userId === userId);
        if (index !== -1) this.users[index] = { ...updatedUser, showDropdown: false };
        this.applyFilters();
        this.successMessage = 'User suspended successfully';
        this.closeConfirmModal();
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        console.error('Error suspending user:', err);
        this.errorMessage = 'Failed to suspend user';
        this.closeConfirmModal();
        setTimeout(() => (this.errorMessage = ''), 3000);
      }
    });
  }

  activateUser(userId: number): void {
    this.userService.activateUser(userId).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.userId === userId);
        if (index !== -1) this.users[index] = { ...updatedUser, showDropdown: false };
        this.applyFilters();
        this.successMessage = 'User activated successfully';
        this.closeConfirmModal();
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        console.error('Error activating user:', err);
        this.errorMessage = 'Failed to activate user';
        this.closeConfirmModal();
        setTimeout(() => (this.errorMessage = ''), 3000);
      }
    });
  }

  deleteUser(userId: number): void {
    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.userId !== userId);
        this.applyFilters();
        this.successMessage = 'User deleted successfully';
        this.closeConfirmModal();
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        console.error('Error deleting user:', err);
        this.errorMessage = 'Failed to delete user';
        this.closeConfirmModal();
        setTimeout(() => (this.errorMessage = ''), 3000);
      }      
    });
  }

  promoteUser(userId: number): void {
    this.userService.promoteUser(userId).subscribe({
    next: (promotedUser) => {
      const index = this.users.findIndex(u => u.userId === userId);
      if (index !== -1) this.users[index] = { ...promotedUser, showDropdown: false };
        this.applyFilters();
        this.successMessage = 'User promoted successfully';
        this.closeConfirmModal();
        setTimeout(() => (this.successMessage = ''), 3000);

      if (promotedUser.suspended) {
        this.userService.activateUser(userId).subscribe({
        next: (activatedUser) => {
          if (index !== -1) this.users[index] = { ...activatedUser, showDropdown: false };
          this.applyFilters();
          this.successMessage = 'User promoted and activated successfully';
          this.closeConfirmModal();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          console.error('Error activating user:', err);
          this.errorMessage = 'User promoted but activation failed';
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
      }
    },
    error: (err) => {
      console.error('Error promoting user:', err);
      this.errorMessage = 'Failed to promote user';
      this.closeConfirmModal();
      setTimeout(() => (this.errorMessage = ''), 3000);
    }
  });
  }

  exportUsersToCSV(): void {
    const csvContent = this.generateUsersCSV();
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `pawportal-users-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
    this.successMessage = 'Users exported successfully!';
    setTimeout(() => (this.successMessage = ''), 3000);
  }

  generateUsersCSV(): string {
    const headers = [
      'User ID',
      'Email',
      'First Name',
      'Last Name',
      'Mobile Number',
      'Gender',
      'Country',
      'Language',
      'Role',
      'Status',
      'Application Count',
      'Created Date'
    ];
    const rows = this.filteredUsers.map(user => [
      user.userId,
      user.email,
      user.firstName,
      user.lastName,
      user.mobileNumber || 'N/A',
      user.gender || 'N/A',
      user.country || 'N/A',
      user.language || 'N/A',
      user.role,
      user.suspended ? 'Suspended' : 'Active',
      user.applicationCount,
      this.formatDate(user.createdAt)
    ]);
    const csvRows = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ];
    return csvRows.join('\n');
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getActionLabel(): string {
    if (!this.confirmAction) return '';
    if (this.confirmAction === 'promote') return 'Promote';
    return this.confirmAction.charAt(0).toUpperCase() + this.confirmAction.slice(1);
  }

  getActionMessage(): string {
    if (!this.selectedUser || !this.confirmAction) return '';
    const name = `${this.selectedUser.firstName} ${this.selectedUser.lastName}`;
    switch (this.confirmAction) {
      case 'suspend':
        return `Are you sure you want to suspend ${name}? They will not be able to log in until reactivated.`;
      case 'activate':
        return `Are you sure you want to activate ${name}? They will be able to log in again.`;
      case 'delete':
        return `Are you sure you want to delete ${name}? This action cannot be undone and will remove all their data.`;
      case 'promote':
        return `Are you sure you want to promote ${name} to Admin? They will have full admin permissions.`;
      default:
        return '';
    }
  }

  @HostListener('document:click', ['$event'])
  handleDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-actions-dropdown') && !target.closest('.user-actions-button')) {
      this.users.forEach(u => u.showDropdown = false);
    }
  }

  @HostListener('window:resize')
  handleResize(): void {
    this.users.forEach(u => { u.showDropdown = false; u.dropdownPosition = undefined; });
  }
}
