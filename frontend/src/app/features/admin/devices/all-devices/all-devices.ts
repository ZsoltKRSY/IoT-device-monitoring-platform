import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { DeviceService } from '../../../../core/services/device.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Device } from '../../../../core/models/device';
import { DeviceDetail } from '../../../../core/models/device-detail';
import { AuthUser } from '../../../../core/models/auth-user';

@Component({
  selector: 'app-all-devices',
  standalone: true,
  imports: [CommonModule, FormsModule, ConfirmDialog],
  templateUrl: './all-devices.html',
  styleUrl: './all-devices.css',
})
export class AllDevices implements OnInit {
  devices: DeviceDetail[] = [];
  filteredDevices: DeviceDetail[] = [];
  expandedDeviceId: number | null = null;
  confirmDevice: DeviceDetail | null = null;

  users: AuthUser[] = [];
  selectedUserId: string = '';

  loading = true;
  error: string | null = null;

  constructor(
    private deviceService: DeviceService,
    private authService: AuthService,
    private toast: ToastService,
    private router: Router
  ) { }

  ngOnInit() {
    this.fetchAllNonAdminUsersAndDevices();
  }

  fetchAllNonAdminUsersAndDevices() {
    this.authService.getAllNonAdminAuthUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.fetchAllDevices();
      },
      error: () => {
        this.toast.error('Failed to fetch user data.', 'Fetch Error');
        this.fetchAllDevices();
      }
    });
  }

  fetchAllDevices() {
    this.loading = true;

    this.deviceService.getAllDevices().subscribe({
      next: (devices) => {
        this.devices = devices.map((device: Device) => {
          const owner = this.users.find((u: any) => u.id === device.userId);
          return {
            id: device.id,
            name: device.name,
            maxConsumption: device.maxConsumption,
            manufacturer: device?.manufacturer ?? '',
            model: device?.model ?? '',
            description: device?.description ?? '',
            owner: owner ?? null,
            expanded: false
          };
        });

        this.filteredDevices = [...this.devices];
        this.loading = false;
      },
      error: () => {
        this.toast.error('Failed to fetch devices.', 'Fetch Error');
        this.loading = false;
      }
    });
  }

  filterDevices() {
    if (!this.selectedUserId) {
      this.filteredDevices = [...this.devices];
    } else {
      const userIdNum = Number(this.selectedUserId);
      this.filteredDevices = this.devices.filter(
        (d) => d.owner?.id === userIdNum
      );
    }
  }

  toggleExpand(device: DeviceDetail) {
    if (this.expandedDeviceId === device.id) {
      this.expandedDeviceId = null;
    } else {
      this.expandedDeviceId = device.id;
    }

    this.filteredDevices.forEach(d => {
      d.expanded = d.id === this.expandedDeviceId;
    });
  }

  onAdd(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/admin/create-device']);
  }

  onEdit(device: DeviceDetail, event: Event) {
    event.stopPropagation();
    this.router.navigate(['/admin/edit-device', device.id]);
  }

  onDelete(user: DeviceDetail, event: Event) {
    event.stopPropagation();
    this.confirmDevice = user;
  }

  deleteDevice(device: DeviceDetail) {
    this.deviceService.deleteDevice(device.id).subscribe({
      next: () => {
        this.toast.success('Successfully deleted device.', 'Delete Success');
        this.devices = this.devices.filter(d => d.id !== device.id);
        this.filteredDevices = this.filteredDevices.filter(d => d.id !== device.id);
        this.confirmDevice = null;
      },
      error: () => {
        this.toast.error('Could not delete selected device.', 'Delete Error');
        this.confirmDevice = null;
      }
    });
  }

}
