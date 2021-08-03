package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.concurrent.TimeUnit;
import com.parkit.parkingsystem.dao.TicketDAO;
import java.util.Date;
import java.util.concurrent.TimeUnit;
public class FareCalculatorService {

	public double calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		Date date1 = ticket.getInTime();
		Date date2 = ticket.getOutTime();

		long diff = date2.getTime() - date1.getTime();

		TicketDAO TD = new TicketDAO();
		int occ = TD.getOcc(ticket.getVehicleRegNumber());

		long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
		long minutes = TimeUnit.SECONDS.toMinutes(seconds);

		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(day);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds));

		long second = TimeUnit.SECONDS.toSeconds(seconds)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds));
		System.out.println("Days : " + day + " Hours :" + hours + " Minutes : " + minute + " Seconds : " + second);

		double ratio = 1;
		if (occ > 1) {
			ratio = 0.95;
		}

		if (minutes > 30) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {

				ticket.setPrice(((minutes) * 1.5 / 60) * ratio);

				break;
			}
			case BIKE: {
				ticket.setPrice(((minutes) * 1.0 / 60) * ratio);

				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(0);
		}

		return ticket.getPrice();
	}
}