import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DogService } from '../../services/dog.service';
import { ApplicationService } from '../../services/application.service';
import { TokenStorageService } from '../../services/token-storage.service';
import { Dog } from '../../models/dog.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dog-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './dog-detail.component.html',
  styleUrl: './dog-detail.component.css'
})
export class DogDetailComponent implements OnInit, OnDestroy {
  dog: Dog | null = null;
  applicationForm!: FormGroup;
  isLoading = true;
  showApplicationForm = false;
  submitted = false;
  successMessage = '';
  errorMessage = '';
  isLoggedIn = false;
  userId: number = 0;
  private userSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dogService: DogService,
    private applicationService: ApplicationService,
    private tokenStorageService: TokenStorageService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.initForm();
    this.checkLoginStatus();
    this.loadDog();
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  checkLoginStatus(): void {
    const token = this.tokenStorageService.getToken();
    this.isLoggedIn = !!token;
    if (this.isLoggedIn) {
      this.userId = this.tokenStorageService.getUsrId();
      this.userSubscription = this.tokenStorageService.getUser().subscribe(user => {
        if (user) {
          this.applicationForm.patchValue({
            firstName: user.firstName || '',
            lastName: user.lastName || '',
            email: user.email || ''
          });
        }
      });
    }
  }

  initForm(): void {
    this.applicationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10,11}$/)]],
      address: ['', [Validators.required, Validators.minLength(10)]],
      city: ['', [Validators.required]],
      postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{4}$/)]],
      occupation: ['', [Validators.required]],
      livingStatus: ['', [Validators.required]],
      reasonForAdoption: ['', [Validators.required, Validators.minLength(50)]],
      experience: ['', [Validators.required, Validators.minLength(20)]]
    });
  }

  loadDog(): void {
    const dogId = Number(this.route.snapshot.paramMap.get('id'));
    this.dogService.getDogById(dogId).subscribe({
      next: (dog) => {
        this.dog = dog;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading dog:', err);
        this.isLoading = false;
        this.router.navigate(['/our-pawls']);
      }
    });
  }

  toggleApplicationForm(): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
    this.showApplicationForm = !this.showApplicationForm;
    if (this.showApplicationForm) {
      setTimeout(() => {
        document.getElementById('application-form')?.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    }
  }

  get f() {
    return this.applicationForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.applicationForm.invalid) {
      return;
    }

    const formData = {
      ...this.applicationForm.value,
      dogId: this.dog?.dogId,
      userId: this.userId
    };

    console.log(this.dog?.dogId);

    this.applicationService.createApplication(formData).subscribe({
      next: (response) => {
        this.successMessage = 'Application submitted successfully! We will review your application and contact you soon.';
        this.applicationForm.reset();
        this.submitted = false;
        setTimeout(() => {
          this.showApplicationForm = false;
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }, 3000);
      },
      error: (err) => {
        console.error('Error submitting application:', err);
        this.errorMessage = 'Failed to submit application. Please try again.';
      }
    });
  }
}