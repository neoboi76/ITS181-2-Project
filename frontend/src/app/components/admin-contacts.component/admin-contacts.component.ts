import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ContactService } from '../../services/contact.service';
import { ContactForm } from '../../models/contact.model';

@Component({
  selector: 'app-admin-contacts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-contacts.component.html',
  styleUrl: './admin-contacts.component.css'
})
export class AdminContactsComponent implements OnInit {
  contacts: ContactForm[] = [];
  filteredContacts: ContactForm[] = [];
  isLoading = true;
  selectedContact: ContactForm | null = null;
  showDetailModal = false;
  searchTerm = '';
  errorMessage = '';

  constructor(private contactService: ContactService) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.loadContacts();
  }

  loadContacts(): void {
    this.contactService.getAllContactForms().subscribe({
      next: (contacts) => {
        this.contacts = contacts.sort((a, b) => 
          new Date(b.submittedAt!).getTime() - new Date(a.submittedAt!).getTime()
        );
        this.filteredContacts = this.contacts;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading contacts:', err);
        this.errorMessage = 'Failed to load contact forms';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredContacts = this.contacts.filter(contact => {
      const matchesSearch = !this.searchTerm || 
        contact.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        contact.email.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        contact.subject.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      return matchesSearch;
    });
  }

  viewDetails(contact: ContactForm): void {
    this.selectedContact = contact;
    this.showDetailModal = true;
    document.body.style.overflow = 'hidden';
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedContact = null;
    document.body.style.overflow = 'auto';
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}