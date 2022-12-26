package com.smart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.BookingRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Booking;
import com.smart.entities.User;
import com.smart.service.BookingService;

@Controller
public class SearchController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private BookingService bookingService;

//	@GetMapping("/search/{query}")
//	public ResponseEntity<?>search(@PathVariable("query")String query,Principal principal){
//		
//		System.out.println(query);
//		
//		User user = this.userRepository.getUserByUserName(principal.getName());
//		List<Booking> bookings = this.bookingRepository.findByUserNameAndUser(query, user);
//		return ResponseEntity.ok(bookings);
//		
//		
//	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchBooking(@RequestParam("query") String query, Model model) {

		
		List<Booking> booking = this.bookingRepository.searchBooking(query);
		model.addAttribute("booking", booking);
		System.out.println("booking :" + booking);
		return "redirect:/normal/booking_detail";

	}
}
