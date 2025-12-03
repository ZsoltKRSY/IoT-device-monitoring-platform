package sd.devices.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceOperationDTO {
    private String name;
    private float maxConsumption;
    private String manufacturer;
    private String model;
    private String description;
    private Long userId;
}
