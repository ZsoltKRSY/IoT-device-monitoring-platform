import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs";
import { DeviceDetail } from "../models/device-detail";
import { Device } from "../models/device";
import { DeviceOperation } from "../models/device-operation";

@Injectable({ providedIn: 'root' })
export class DeviceService {
    private baseUrl = `${environment.apiUrl}/devices`;

    constructor(private http: HttpClient) { }

    getAllDevices(): Observable<Device[]> {
        return this.http.get<Device[]>(`${this.baseUrl}`);
    }

    getAllDevicesOfUser(userId: number): Observable<Device[]> {
        return this.http.get<Device[]>(`${this.baseUrl}/user/${userId}`);
    }

    getDeviceById(id: number): Observable<Device> {
        return this.http.get<Device>(`${this.baseUrl}/${id}`);
    }

    createDevice(device: DeviceOperation): Observable<DeviceDetail> {
        return this.http.post<DeviceDetail>(`${this.baseUrl}`, device);
    }

    updateDevice(id: number, device: DeviceOperation): Observable<DeviceDetail> {
        return this.http.put<DeviceDetail>(`${this.baseUrl}/${id}`, device);
    }

    deleteDevice(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

}