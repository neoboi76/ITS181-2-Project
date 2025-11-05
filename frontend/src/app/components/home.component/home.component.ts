import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DogService } from '../../services/dog.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  featuredDogs: any[] = [];
  isLoading = true;

  constructor(
    private dogService: DogService,
    private router: Router
  ) {}

  ngOnInit(): void {
        window.scrollTo(0, 0);
        this.loadFeaturedDogs();
    }

  loadFeaturedDogs(): void {
    this.dogService.getAvailableDogs().subscribe({
        next: (dogs) => {
            this.featuredDogs = dogs.sort(() => 0.5 - Math.random()).slice(0, 3);
            this.isLoading = false;
        },
        error: (err) => {
            console.error('Error loading dogs:', err);
            this.isLoading = false;
        }
    });
  }

  viewDog(dogId: number): void {
    window.scrollTo(0, 0);
    this.router.navigate(['/our-pawls', dogId]);
  }
}