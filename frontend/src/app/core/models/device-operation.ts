export interface DeviceOperation {
    name: string;
    maxConsumption: number;
    userId: number | null;
    manufacturer?: string;
    model?: string;
    description?: string;
}