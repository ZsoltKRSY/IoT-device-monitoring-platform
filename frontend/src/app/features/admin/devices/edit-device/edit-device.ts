import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { DeviceService } from '../../../../core/services/device.service';
import { DeviceOperation } from '../../../../core/models/device-operation';
import { AuthUser } from '../../../../core/models/auth-user';

@Component({
  selector: 'app-edit-device',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './edit-device.html',
  styleUrl: './edit-device.css',
})
export class EditDevice implements OnInit {
  deviceId!: number;

  name = '';
  maxConsumption = '';
  manufacturer = '';
  model = ''
  description = '';
  ownerId: number | null = null;
  users: AuthUser[] = [];

  loading = false;

  nameError: string | null = null;
  maxConsumptionError: string | null = null;
  editError: string | null = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private auth: AuthService,
    private devices: DeviceService,
    private toast: ToastService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.deviceId = +id;
        this.fetchDeviceDetails();
      }
    });

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

  fetchDeviceDetails() {
    this.devices.getDeviceById(this.deviceId).subscribe({
      next: (device) => {
        this.name = device.name;
        this.maxConsumption = device.maxConsumption.toString();
        this.manufacturer = device.manufacturer;
        this.model = device.model;
        this.description = device.description;
        this.ownerId = device.userId;
      },
      error: () => {
        this.toast.error('Failed to fetch device data.', 'Fetch Error');
      }
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

    const updatedDevice: DeviceOperation = {
      name: this.name,
      maxConsumption: parsedConsumption,
      manufacturer: this.manufacturer,
      model: this.model,
      description: this.description,
      userId: this.ownerId,
    }

    this.devices.updateDevice(this.deviceId, updatedDevice).subscribe({
      next: () => {
        this.toast.success('Device updated successfully.', 'Update Success');
        this.router.navigate(['/admin/devices']);
        this.loading = false;
      },
      error: () => {
        this.editError = 'Failed to update device, please try again later';
        this.loading = false;
      }
    });
  }
}
