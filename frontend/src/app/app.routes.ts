import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';
import { loginRegisterGuard } from './core/guards/login-register-guard';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'home', loadComponent: () => import('./features/home/home').then(m => m.Home), canActivate: [authGuard] },
    { path: 'login', loadComponent: () => import('./features/auth/login/login').then(m => m.Login), canActivate: [loginRegisterGuard] },
    { path: 'register', loadComponent: () => import('./features/auth/register/register').then(m => m.Register), canActivate: [loginRegisterGuard] },
    { path: 'admin/users', loadComponent: () => import('./features/admin/users/all-users/all-users').then(m => m.AllUsers), canActivate: [adminGuard] },
    { path: 'admin/devices', loadComponent: () => import('./features/admin/devices/all-devices/all-devices').then(m => m.AllDevices), canActivate: [adminGuard] },
    { path: 'admin/customer-service', loadComponent: () => import('./features/admin/customer-service/customer-service').then(m => m.CustomerService), canActivate: [adminGuard] },
    { path: 'admin/create-device', loadComponent: () => import('./features/admin/devices/create-device/create-device').then(m => m.CreateDevice), canActivate: [adminGuard] },
    { path: 'admin/edit-device/:id', loadComponent: () => import('./features/admin/devices/edit-device/edit-device').then(m => m.EditDevice), canActivate: [adminGuard] },
    { path: 'admin/create-user', loadComponent: () => import('./features/admin/users/create-user/create-user').then(m => m.CreateUser), canActivate: [adminGuard] },
    { path: 'admin/edit-user/:id', loadComponent: () => import('./features/admin/users/edit-user/edit-user').then(m => m.EditUser), canActivate: [adminGuard] },
    { path: 'my-devices', loadComponent: () => import('./features/my-devices/my-devices').then(m => m.MyDevices), canActivate: [authGuard] },
    { path: 'my-consumption', loadComponent: () => import('./features/consumption/consumption').then(m => m.Consumption), canActivate: [authGuard] },
    { path: '**', redirectTo: 'home' }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }