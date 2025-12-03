package sd.monitoring.dtos.mappers;

import sd.monitoring.dtos.ConsumptionDTO;
import sd.monitoring.entities.Consumption;

public class ConsumptionMapper {

    public static ConsumptionDTO toConsumptionDto(Consumption consumption) {
        return ConsumptionDTO.builder()
                .id(consumption.getId())
                .deviceId(consumption.getDevice().getId())
                .day(consumption.getDay())
                .hour(consumption.getHour())
                .totalConsumption(consumption.getTotalConsumption())
                .measurementCount(consumption.getMeasurementCount())
                .build();
    }

}
