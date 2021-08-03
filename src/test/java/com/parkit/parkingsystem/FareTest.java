package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.parkit.parkingsystem.constants.Fare;

class FareTest {
	public static Fare fare;
	
	@Test
	void carTest() {
		Fare fare = new Fare();
		double car = fare.CAR_RATE_PER_HOUR;
		assertEquals(car,1.5);
	}
	@Test
	void bikeTest() {
		Fare fare = new Fare();
		double bike = fare.BIKE_RATE_PER_HOUR;
		assertEquals(bike,1.0);
	}


}
