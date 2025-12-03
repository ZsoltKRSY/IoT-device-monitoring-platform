import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartOptions, ChartData, ChartDataset } from 'chart.js';
import { AuthService } from '../../core/services/auth.service';
import { DeviceService } from '../../core/services/device.service';
import { ConsumptionService } from '../../core/services/consumption.service';
import { ToastService } from '../../core/services/toast.service';
import { AuthUser } from '../../core/models/auth-user';
import { Device } from '../../core/models/device';
import { DeviceConsumption } from '../../core/models/device-consumption';

@Component({
  selector: 'app-consumption',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './consumption.html',
  styleUrl: './consumption.css',
})
export class Consumption implements OnInit {
  devices: Device[] = [];
  checked: { [key: number]: boolean } = {};
  deviceColors: { [key: number]: string } = {};
  deviceConsumption: { [key: number]: DeviceConsumption[] } = {};
  selectedDay: string = new Date().toISOString().split('T')[0];

  currentUser: AuthUser | null = null;

  loading = true;
  error: string | null = null;

  //chart
  chartData: ChartData<'line'> = { labels: [], datasets: [] };

  chartOptions: ChartOptions<'line'> = {
    responsive: true,
    animation: false,
    plugins: {
      legend: { position: 'top' },
      tooltip: { mode: 'index', intersect: false }
    },
    scales: {
      x: { title: { display: true, text: 'Hour' } },
      y: { title: { display: true, text: 'Energy (kWh)' } }
    }
  };

  constructor(
    private authService: AuthService,
    private deviceService: DeviceService,
    private consumptionService: ConsumptionService,
    private toast: ToastService
  ) { }

  ngOnInit() {
    this.fetchUserDevicesAndConsumption();
  }

  fetchUserDevicesAndConsumption() {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.fetchDevicesOfUser(this.currentUser.id);
    }
  }

  fetchDevicesOfUser(userId: number) {
    this.deviceService.getAllDevicesOfUser(userId).subscribe({
      next: (devices) => {
        this.devices = devices;

        this.devices.forEach(device => {
          this.deviceColors[device.id] = this.getColorForDevice(device.id);
          this.checked[device.id] = true;
        });

        this.fetchDeviceConsumption();
      },
      error: () => {
        this.toast.error('Failed to fetch devices.', 'Fetch Error');
      }
    });
  }

  fetchDeviceConsumption() {
    this.loading = true;
    this.deviceConsumption = {};

    let pending = this.devices.length;

    if (pending === 0) {
      this.loading = false;
    }

    this.devices.forEach(device => {
      this.consumptionService
        .getDeviceConsumptionForDay(device.id, this.selectedDay)
        .subscribe({
          next: (consumption) => {
            this.deviceConsumption[device.id] = consumption;

            pending--;
            if (pending === 0) {
              this.loading = false;
              this.updateChart();
            }
          },
          error: () => {
            this.toast.error(`Failed to fetch consumption for ${device.name}.`);
            pending--;
            if (pending === 0) {
              this.loading = false;
              this.updateChart();
            }
          }
        });
    });
  }

  updateChart() {
    const labels = Array.from({ length: 24 }, (_, i) => i.toString());
    const datasets: ChartDataset<'line'>[] = [];

    this.devices.forEach(device => {
      if (this.checked[device.id] && this.deviceConsumption[device.id]) {

        const color = this.deviceColors[device.id];

        datasets.push({
          label: device.name,
          data: this.deviceConsumption[device.id].map(d => d.totalConsumption),
          backgroundColor: color + "88", // semi-transparent
          borderColor: color,
          borderWidth: 2
        });
      }
    });

    this.chartData = { labels, datasets };
  }


  private getColorForDevice(deviceId: number): string {
    let hash = deviceId;
    hash = ((hash >> 16) ^ hash) * 0x45d9f3b;
    hash = ((hash >> 16) ^ hash) * 0x45d9f3b;
    hash = (hash >> 16) ^ hash;

    const r = (hash & 0xFF0000) >> 16;
    const g = (hash & 0x00FF00) >> 8;
    const b = hash & 0x0000FF;

    return `#${(r & 0xFF).toString(16).padStart(2, '0')}${(g & 0xFF)
      .toString(16)
      .padStart(2, '0')}${(b & 0xFF).toString(16).padStart(2, '0')}`;
  }

  toggleDevice(deviceId: number) {
    this.checked[deviceId] = !this.checked[deviceId];
    this.updateChart();
  }

  onDayChange(event: any) {
    this.selectedDay = event.target.value;
    this.fetchDeviceConsumption();
  }

}
