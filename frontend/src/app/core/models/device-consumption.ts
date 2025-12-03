export interface DeviceConsumption {
    id: number;
    deviceId: number;
    day: Date;
    hour: number;
    totalConsumption: number;
    measurementCount: number;
}