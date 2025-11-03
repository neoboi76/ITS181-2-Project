import { Routes } from '@angular/router';
import { AuthGuard } from './services/auth-guard';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { ResetPasswordComponent } from './components/reset-password/reset-password';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password';

export const routes: Routes = [
     { path: '', redirectTo: '/login', pathMatch: 'full'}, 
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent },
    //{ path: 'home', component: HomeComponent, canActivate: [AuthGuard]}, 
    { path: 'reset-password', component: ResetPasswordComponent},
    { path: 'forgot-password', component: ForgotPasswordComponent}
   // { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard]},
];
