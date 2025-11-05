import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { TokenStorageService } from '../../services/token-storage.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit, OnDestroy {
  settingsForm!: FormGroup;
  submitted = false;
  successMessage = '';
  errorMessage = '';
  userId: number = 0;
  isLoading = true;
  private userSubscription?: Subscription;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenStorageService: TokenStorageService
  ) {}

  ngOnInit(): void {
    window.scrollTo(0, 0);
    this.userId = this.tokenStorageService.getUsrId();
    this.initForm();
    this.loadUserData();
  }
  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  initForm(): void {
    this.settingsForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      mobileNumber: ['', [Validators.pattern(/^[0-9]{10,11}$/)]],
      gender: [''],
      country: [''],
      language: ['']
    });
  }

  loadUserData(): void {
    this.authService.getUser(this.userId).subscribe({
      next: (user: any) => {
        this.settingsForm.patchValue({
          firstName: user.firstName || '',
          lastName: user.lastName || '',
          mobileNumber: user.mobileNumber || '',
          gender: user.gender || '',
          country: user.country || '',
          language: user.language || ''
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading user data:', err);
        this.errorMessage = 'Failed to load user data';
      }
    });

  }

  get f() {
    return this.settingsForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.settingsForm.invalid) {
      return;
    }

    const formData = {
      ...this.settingsForm.value,
      id: this.userId
    };

    this.authService.settingsForm(
      formData.firstName,
      formData.lastName,
      formData.id,
      formData.gender,
      formData.country,
      formData.language,
      formData.mobileNumber
    ).subscribe({
      next: (response) => {
        this.successMessage = 'Settings updated successfully!';
        
        const currentUser = this.tokenStorageService.getUserSync();
        if (currentUser) {
          currentUser.firstName = formData.firstName;
          currentUser.lastName = formData.lastName;
          this.tokenStorageService.saveUser(currentUser);
        }

        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (err) => {
        console.error('Error updating settings:', err);
        this.errorMessage = 'Failed to update settings. Please try again.';
      }
    });
  }
}