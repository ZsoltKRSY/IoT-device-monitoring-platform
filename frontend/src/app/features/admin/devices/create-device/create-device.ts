import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { DeviceService } from '../../../../core/services/device.service';
import { DeviceOperation } from '../../../../core/models/device-operation';
import { AuthUser } from '../../../../core/models/auth-user';

@Component({
  selector: 'app-create-device',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-device.html',
  styleUrl: './create-device.css',
})
export class CreateDevice implements OnInit {
  name = '';
  maxConsumption = '';
  manufacturer = '';
  model = '';
  description = '';
  ownerId = null;
  users: AuthUser[] = [];

  loading = false;

  nameError: string | null = null;
  maxConsumptionError: string | null = null;
  createError: string | null = null;

  constructor(
    private router: Router,
    private auth: AuthService,
    private devices: DeviceService,
    private toast: ToastService
  ) { }

  ngOnInit() {
    this.fetchAllNonAdminUsers();
  }

  fetchAllNonAdminUsers() {
    this.auth.getAllNonAdminAuthUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: () => {
        this.toast.error('Failed to fetch user data.', 'Fetch Error');
      },
    });
  }

  onSubmit(form: NgForm) {
    this.nameError = null;
    this.maxConsumptionError = null;

    if (!this.name) {
      this.nameError = 'Device name is required';
    }

    const parsedConsumption = parseFloat(this.maxConsumption as any);
    if (this.maxConsumption === '' || isNaN(parsedConsumption) || parsedConsumption < 0 ||
      !/^\d+(\.\d{1,3})?$/.test(this.maxConsumption)
    ) {
      this.maxConsumptionError = 'Must be a positive number with up to 3 decimals';
    }

    if (this.nameError || this.maxConsumptionError) {
      return;
    }

    this.loading = true;

    const newDevice: DeviceOperation = {
      name: this.name,
      maxConsumption: parsedConsumption,
      manufacturer: this.manufacturer,
      model: this.model,
      description: this.description,
      userId: this.ownerId,
    }

    this.devices.createDevice(newDevice).subscribe({
      next: () => {
        this.toast.success('New device added successfully.', 'Create Success');
        this.router.navigate(['/admin/devices']);
        this.loading = false;
      },
      error: () => {
        this.createError = 'Failed to create device, please try again later';
        this.loading = false;
      }
    });
  }
}
