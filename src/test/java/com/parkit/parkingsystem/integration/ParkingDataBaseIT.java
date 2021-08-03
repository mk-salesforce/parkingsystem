package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingService parkingService;
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private Ticket ticket;
	Ticket ticket1;
	private Date inTime;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		ticket = new Ticket();
		ticket1 = new Ticket();
		inTime = new Date();
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	public void testParkingACar() {
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			ticket.setPrice(0);
			ticket.setInTime(inTime);
			ticket.setOutTime(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		parkingService.processIncomingVehicle();
	}

	@Test
	public void testParkingLotExitOfACar() throws Exception {
		testParkingACar();
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AB123AB");
		try {
			ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = "AB123AB";
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);
				ticket1.setParkingSpot(parkingSpot);
				ticket1.setVehicleRegNumber(vehicleRegNumber);
				ticket1.setPrice(0);
				inTime.setTime(System.currentTimeMillis() - (120 * 60 * 1000));
				ticket1.setInTime(inTime);
				ticket1.setOutTime(null);
				ticketDAO.saveTicket(ticket1);
				System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
				System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		parkingService.processExitingVehicle();
		Ticket ticket2 = new Ticket();
		ticket2 = ticketDAO.getTicket("AB123AB");
		assertTrue(ticketDAO.updateTicket(ticket2));
		assertNotNull(ticket2.getOutTime());
		assertNotNull(ticket2.getPrice());
	}
}
