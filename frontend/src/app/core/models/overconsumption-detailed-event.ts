export interface OverconsumptionDetailedEvent {
    deviceId: number;
    deviceName: string;
    userId: number;
    day: Date;
    hour: number;
    currentConsumption: number;
    maxConsumption: number;
    measurementCount: number;
}
