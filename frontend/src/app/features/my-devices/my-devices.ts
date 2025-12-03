import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../core/services/device.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { Device } from '../../core/models/device';
import { DeviceDetail } from '../../core/models/device-detail';
import { AuthUser } from '../../core/models/auth-user';

@Component({
  selector: 'app-my-devices',
  imports: [CommonModule],
  templateUrl: './my-devices.html',
  styleUrl: './my-devices.css',
})
export class MyDevices implements OnInit {
  devices: DeviceDetail[] = [];
  expandedDeviceId: number | null = null;

  currentUser: AuthUser | null = null;

  loading = true;
  error: string | null = null;

  constructor(
    private deviceService: DeviceService,
    private authService: AuthService,
    private toast: ToastService
  ) { }

  ngOnInit() {
    this.fetchUserAndDevices();
  }

  fetchUserAndDevices() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.fetchDevicesOfUser(this.currentUser.id);
    }
  }

  fetchDevicesOfUser(userId: number) {
    this.loading = true;

    this.deviceService.getAllDevicesOfUser(userId).subscribe({
      next: (devices) => {
        this.devices = devices.map((device: Device) => {
          return {
            id: device.id,
            name: device.name,
            maxConsumption: device.maxConsumption,
            manufacturer: device?.manufacturer ?? '',
            model: device?.model ?? '',
            description: device?.description ?? '',
            owner: this.currentUser,
            expanded: false
          };
        });

        this.loading = false;
      },
      error: () => {
        this.toast.error('Failed to fetch devices.', 'Fetch Error');
        this.loading = false;
      }
    });
  }

  toggleExpand(device: DeviceDetail) {
    if (this.expandedDeviceId === device.id) {
      this.expandedDeviceId = null;
    } else {
      this.expandedDeviceId = device.id;
    }

    this.devices.forEach(d => {
      d.expanded = d.id === this.expandedDeviceId;
    });
  }

}
