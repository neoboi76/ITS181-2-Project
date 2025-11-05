import { AdminDashboardComponent } from './components/admin-dashboard.component/admin-dashboard.component';
import { AdminDogsComponent } from './components/admin-dogs.component/admin-dogs.component';
import { AdminApplicationsComponent } from './components/admin-applications.component/admin-applications.component';
import { AdminContactsComponent } from './components/admin-contacts.component/admin-contacts.component';
import { AdminUsersComponent } from './components/admin-users.component/admin-users.component';
import { AdminAuditLogsComponent } from './components/admin-audit-logs.component/admin-audit-logs.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { HomeComponent } from './components/home.component/home.component';
import { OurPawlsComponent } from './components/our-pawls.component/our-pawls.component';
import { AuthGuard } from './services/auth-guard.service';
import { DogDetailComponent } from './components/dog-detail.component/dog-detail.component';
import { AboutComponent } from './components/about.component/about.component';
import { ContactComponent } from './components/contact.component/contact.component';
import { SettingsComponent } from './components/settings.component/settings.component';
import { PrivacyPolicyComponent } from './components/privacy-policy.component/privacy-policy.component';
import { TermsOfServiceComponent } from './components/terms-of-service.component/terms-of-service.component';
import { AdminGuard } from './services/admin-guard.service';
import { Routes } from '@angular/router';

export const routes: Routes = [

    { path: '', redirectTo: '/home', pathMatch: 'full'}, 
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent },
    { path: 'reset-password', component: ResetPasswordComponent},
    { path: 'forgot-password', component: ForgotPasswordComponent},
    { path: 'home', component: HomeComponent}, 
    { path: 'our-pawls', component: OurPawlsComponent},
    { path: 'our-pawls/:id', component: DogDetailComponent, canActivate: [AuthGuard]},
    { path: 'about', component: AboutComponent},
    { path: 'contact', component: ContactComponent},
    { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard]},
    { path: 'privacy-policy', component: PrivacyPolicyComponent},
    { path: 'terms-of-service', component: TermsOfServiceComponent},
    
    { 
        path: 'admin', 
        canActivate: [AdminGuard],
        children: [
            { path: '', component: AdminDashboardComponent },
            { path: 'dogs', component: AdminDogsComponent },
            { path: 'applications', component: AdminApplicationsComponent },
            { path: 'contacts', component: AdminContactsComponent },
            { path: 'users', component: AdminUsersComponent },
            { path: 'audit-logs', component: AdminAuditLogsComponent }
        ]
    },
    
    { path: '**', redirectTo: '/home' }
];