package sd.device_data_simulator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sd.device_data_simulator.service.SimulationService;

import java.util.Scanner;

@SpringBootApplication
public class DeviceDataSimulatorApplication implements CommandLineRunner {

	private final SimulationService simulationService;
	private final ApplicationContext context;

	public DeviceDataSimulatorApplication(SimulationService simulationService, ApplicationContext context) {
		this.simulationService = simulationService;
		this.context = context;
	}

	public static void main(String[] args) {
		SpringApplication.run(DeviceDataSimulatorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			try {
				System.out.print("How many measurements do you want to generate? (0 to exit)\n");
				int count = scanner.nextInt();

				if (count == 0) {
					System.out.println("Exiting simulator and shutting down Spring Boot…");
					int exitCode = SpringApplication.exit(context, () -> 0);
					System.exit(exitCode);
					break;
				}

				System.out.print("Delay between generations in seconds (0 for instant generation):\n");
				long delaySeconds = scanner.nextLong() * 1000L;

				for (int i = 0; i < count; i++) {
					simulationService.generateMeasurements(1);

					System.out.println("Measurements " + (i + 1) + " generated successfully!\n");

					if (delaySeconds > 0) {
						Thread.sleep(delaySeconds);
					}
				}

				System.out.println("All measurements have been generated successfully!\n");
			} catch (Exception ex) {
				System.err.println("Error: " + ex.getMessage());
				System.out.println("Trying again...\n");

				scanner.nextLine();
			}
		}
	}
}
