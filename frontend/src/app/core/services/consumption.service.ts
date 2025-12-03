import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs";
import { DeviceConsumption } from "../models/device-consumption";

@Injectable({ providedIn: 'root' })
export class ConsumptionService {
    private baseUrl = `${environment.apiUrl}/monitoring`;

    constructor(private http: HttpClient) { }

    getDeviceConsumptionForDay(deviceId: number, day: string): Observable<DeviceConsumption[]> {
        return this.http.get<DeviceConsumption[]>(`${this.baseUrl}/` + deviceId + "?day=" + day);
    }

}