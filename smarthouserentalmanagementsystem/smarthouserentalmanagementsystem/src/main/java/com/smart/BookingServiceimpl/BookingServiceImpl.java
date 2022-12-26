package com.smart.BookingServiceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.smart.dao.BookingRepository;
import com.smart.entities.Booking;
import com.smart.service.BookingService;


@Configuration
public class BookingServiceImpl implements BookingService {
	
	@Autowired
	private BookingRepository bookingRepository;
	
	
	 @Override
	    public List<Booking> searchBooking(String query) {
	        List<Booking> booking = bookingRepository.searchBooking(query);
	        return booking;
	    }

}
