import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DogService } from '../../services/dog.service';
import { Dog } from '../../models/dog.model';

@Component({
  selector: 'app-our-pawls',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './our-pawls.component.html',
  styleUrl: './our-pawls.component.css'
})
export class OurPawlsComponent implements OnInit {
  dogs: Dog[] = [];
  filteredDogs: Dog[] = [];
  isLoading = true;
  searchTerm = '';
  selectedSize = '';
  selectedGender = '';

  constructor(
    private dogService: DogService,
    private router: Router
  ) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.loadDogs();
  }

  loadDogs(): void {
    this.dogService.getAvailableDogs().subscribe({
      next: (dogs) => {
        this.dogs = dogs;
        this.filteredDogs = dogs;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading dogs:', err);
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredDogs = this.dogs.filter(dog => {
      const matchesSearch = !this.searchTerm || 
        dog.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        dog.breed.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesSize = !this.selectedSize || dog.size === this.selectedSize;
      const matchesGender = !this.selectedGender || dog.gender === this.selectedGender;

      return matchesSearch && matchesSize && matchesGender;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedSize = '';
    this.selectedGender = '';
    this.filteredDogs = this.dogs;
  }

  viewDog(dogId: number): void {
    window.scrollTo(0, 0);
    this.router.navigate(['/our-pawls', dogId]);
  }
}