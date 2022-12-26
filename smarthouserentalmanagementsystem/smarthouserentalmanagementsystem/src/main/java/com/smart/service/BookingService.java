package com.smart.service;

import java.util.List;

import com.smart.entities.Booking;

public interface BookingService {
	
	List<Booking> searchBooking(String query);

}
