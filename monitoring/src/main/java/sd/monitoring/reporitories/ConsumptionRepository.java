package sd.monitoring.reporitories;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.monitoring.entities.Consumption;
import sd.monitoring.entities.Device;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {

    Optional<Consumption> findByDeviceAndDayAndHour(Device device, LocalDate day, Integer hour);

    List<Consumption> findByDeviceAndDay(Device device, LocalDate day);

}