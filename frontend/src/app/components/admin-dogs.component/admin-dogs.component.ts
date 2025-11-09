import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DogService } from '../../services/dog.service';
import { Dog } from '../../models/dog.model';

@Component({
  selector: 'app-admin-dogs',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './admin-dogs.component.html',
  styleUrl: './admin-dogs.component.css'
})
export class AdminDogsComponent implements OnInit {
  dogs: Dog[] = [];
  filteredDogs: Dog[] = [];
  isLoading = true;
  showModal = false;
  isEditMode = false;
  dogForm!: FormGroup;
  selectedDog: Dog | null = null;
  searchTerm = '';
  filterStatus = '';
  successMessage = '';
  errorMessage = '';
  showDeleteConfirm = false;
  dogToDelete: Dog | null = null;
  isSubmitted = false;

  constructor(
    private dogService: DogService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.initForm();
    this.loadDogs();
  }

  initForm(): void {
    this.dogForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      breed: ['', [Validators.required]],
      age: [1, [Validators.required, Validators.min(0), Validators.max(20)]],
      size: ['MEDIUM', [Validators.required]],
      gender: ['Male', [Validators.required]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      temperament: [''],
      imageUrl: ['', [Validators.required]],
      vaccinated: [true],
      spayedNeutered: [true],
      status: ['AVAILABLE', [Validators.required]]
    });
  }

  loadDogs(): void {
    this.dogService.getAllDogs().subscribe({
      next: (dogs) => {
        this.dogs = dogs;
        this.filteredDogs = dogs;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading dogs:', err);
        this.errorMessage = 'Failed to load dogs';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredDogs = this.dogs.filter(dog => {
      const matchesSearch = !this.searchTerm || 
        dog.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        dog.breed.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = !this.filterStatus || dog.status === this.filterStatus;

      return matchesSearch && matchesStatus;
    });
  }

  openAddModal(): void {
    this.isEditMode = false;
    this.selectedDog = null;
    this.successMessage = '';
    this.errorMessage = '';
    this.dogForm.reset({
      age: 1,
      size: 'MEDIUM',
      gender: 'Male',
      vaccinated: true,
      spayedNeutered: true,
      status: 'AVAILABLE'
    });
    this.showModal = true;
    document.body.style.overflow = 'hidden';
  }

  openEditModal(dog: Dog): void {
    this.isEditMode = true;
    this.selectedDog = dog;
    this.successMessage = '';
    this.errorMessage = '';
    this.dogForm.patchValue(dog);
    this.showModal = true;
    document.body.style.overflow = 'hidden';
  }

  closeModal(): void {
    this.showModal = false;
    this.dogForm.reset();
    this.successMessage = '';
    this.errorMessage = '';
    document.body.style.overflow = 'auto';
  }

  submitForm(): void {
     this.isSubmitted = true;
    if (this.dogForm.invalid) {
      Object.keys(this.dogForm.controls).forEach(key => {
        const control = this.dogForm.get(key);
        if (control && control.invalid) {
          control.markAsTouched();
        }
      });
      return;
    }

    const dogData = this.dogForm.value;

    if (this.isEditMode && this.selectedDog) {
      this.dogService.updateDog(this.selectedDog.dogId, dogData).subscribe({
        next: (updatedDog) => {
          const index = this.dogs.findIndex(d => d.dogId === updatedDog.dogId);
          if (index !== -1) {
            this.dogs[index] = updatedDog;
          }
          this.applyFilters();
          this.successMessage = 'Dog updated successfully!';
          setTimeout(() => this.closeModal(), 500);
        },
        error: (err) => {
          console.error('Error updating dog:', err);
          this.errorMessage = 'Failed to update dog';
        }
      });
    } else {
      this.dogService.createDog(dogData).subscribe({
        next: (newDog) => {
          this.dogs.unshift(newDog);
          this.applyFilters();
          this.successMessage = 'Dog added successfully!';
          setTimeout(() => this.closeModal(), 500);
        },
        error: (err) => {
          console.error('Error creating dog:', err);
          this.errorMessage = 'Failed to add dog';
        }
      });
    }
  }

  confirmDelete(dog: Dog): void {
    this.dogToDelete = dog;
    this.showDeleteConfirm = true;
    document.body.style.overflow = 'hidden';
  }

  cancelDelete(): void {
    this.dogToDelete = null;
    this.showDeleteConfirm = false;
    document.body.style.overflow = 'auto';
  }

  deleteDog(): void {
    if (!this.dogToDelete) return;

    this.dogService.deleteDog(this.dogToDelete.dogId).subscribe({
      next: () => {
        this.dogs = this.dogs.filter(d => d.dogId !== this.dogToDelete!.dogId);
        this.applyFilters();
        this.cancelDelete();
      },
      error: (err) => {
        console.error('Error deleting dog:', err);
        this.errorMessage = 'Failed to delete dog';
        this.cancelDelete();
      }
    });
  }

  get f() {
    return this.dogForm.controls;
  }

  exportDogsToCSV(): void {
    const csvContent = this.generateDogsCSV();
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `pawportal-dogs-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
    
    this.successMessage = 'Dogs exported successfully!';
    setTimeout(() => this.successMessage = '', 3000);
  }

  generateDogsCSV(): string {
    const headers = [
      'Dog ID',
      'Name',
      'Breed',
      'Age',
      'Gender',
      'Size',
      'Status',
      'Vaccinated',
      'Spayed/Neutered',
      'Temperament',
      'Description',
      'Image URL',
      'Created Date',
      'Updated Date'
    ];

    const rows = this.filteredDogs.map(dog => [
      dog.dogId,
      dog.name,
      dog.breed,
      dog.age,
      dog.gender,
      dog.size,
      dog.status,
      dog.vaccinated ? 'Yes' : 'No',
      dog.spayedNeutered ? 'Yes' : 'No',
      dog.temperament || 'N/A',
      dog.description.replace(/"/g, '""'), // Escape quotes in description
      dog.imageUrl,
      new Date(dog.createdAt).toLocaleDateString('en-US'),
      new Date(dog.updatedAt).toLocaleDateString('en-US')
    ]);

    const csvRows = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ];

    return csvRows.join('\n');
  }

  clearFilters(): void {
     this.searchTerm = '';
     this.filterStatus = '';
     this.filteredDogs = this.dogs;
   }

}

