package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.BookingRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Booking;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	
	//Method for adding commom data to response
	@ModelAttribute
	public void addCommomData(Model model,Principal principal) {
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER"+ user );
		
		model.addAttribute("user", user);
	}
	
	
	//Dashboard Home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		
		return "normal/user_dashboard";
	}
	
	
	
	//Open Add Booking Form
	@GetMapping("/add-booking")
	public String  openAddBooking(Model model) {
		
		model.addAttribute("title", "Add Booking");
		model.addAttribute("booking", new Booking());
		
		return "normal/add_booking_form";
	}
	
	//Processing add Booking form
	@PostMapping("/process-booking")
	public String processBooking(@ModelAttribute Booking booking
			,@RequestParam("profileimage")MultipartFile file,
			Principal principal
			,HttpSession session) {
		
	try {
		String name = principal.getName();
		User user= this.userRepository.getUserByUserName(name);
		
		//Processing and Uploading file
		
		if(file.isEmpty()) {
			System.out.println("File is empty");
			booking.setImage("booking.png"); 
			
		}else {
			booking.setImage(file.getOriginalFilename());
			File savefile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(savefile.getAbsolutePath()+File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		
		booking.setUser(user);
		user.getBooking().add(booking);
		this.userRepository.save(user);
		System.out.println("Added to database");
		
		//message success
		session.setAttribute("message", new Message("Your Booking Details are added Successfully!! Add more","success"));
		
		
		
		System.out.println("DATA"+booking);
	} catch (Exception e) {
		System.out.println("ERROR"+ e.getLocalizedMessage());
		e.printStackTrace();
		
		//Error Message
		session.setAttribute("message", new Message("Something Went Wrong!! Try Again","error"));

	}
		return  "normal/add_booking_form";
		
	}
	
	//Show Bookings
	@GetMapping("/show_booking/{page}")
	public String showBooking(@PathVariable("page")Integer page,Model model,Principal principal) {
		
		model.addAttribute("title", "Show Booking Details");
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		Pageable pageable = PageRequest.of(page, 3);
		Page <Booking> booking = this.bookingRepository.findBookingByUser(user.getId(),pageable);
		model.addAttribute("booking",booking);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",booking.getTotalPages());
		return "normal/show_booking";
	}
	
	//Showing particular booking details
	
	@RequestMapping("/booking/{bid}")
	public String showBookingDetails(@PathVariable("bid")Integer bid, Model model,Principal principal) {
		
		Optional<Booking> bookingOptional =this.bookingRepository.findById(bid);
		Booking booking = bookingOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId()== booking.getUser().getId())
		{
			model.addAttribute("booking",booking);
		}else{
			System.out.println("Error");
		}
		
			
		
		
		return "normal/booking_detail";
		
	}
	
	//Delete Booking
	@GetMapping("/delete/{bid}")
	public String deleteBookingById(@PathVariable("bid")Integer bid,Model model,HttpSession session,Principal principal) {
		
		Optional<Booking> bookingOptional= this.bookingRepository.findById(bid);
		Booking booking = bookingOptional.get();
		//booking.setUser(null);
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getBooking().remove(booking);
		this.userRepository.save(user);
		
		
		System.out.println("Deleted");
		
		session.setAttribute("message", new Message("Booking Details Deleted Successfully !!","success"));
		return "redirect:/user/show_booking/0";
		
	}
	
	//Open Update Form
	@PostMapping("/update_booking/{bid}")
	public String updateBooking(@PathVariable("bid")Integer bid, Model model) {
		model.addAttribute("title","Update Booking");
		Booking booking = this.bookingRepository.findById(bid).get();
		model.addAttribute("booking", booking);
		return "normal/update_booking";
		
	}

	
	//Handle updated data to save
	@RequestMapping(value = "/process_update",method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Booking booking,@RequestParam("profileimage")MultipartFile file,
			Model model,
			HttpSession session,Principal principal){
		
		try {
			
			//old booking details
			 Booking oldbookingDetail  = this.bookingRepository.findById(booking.getBid()).get();
			 
			
			//image
			if(!file.isEmpty())
			{
				//File Work
				//Rewrite
				
				//delete old photo
				File deletefile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deletefile,oldbookingDetail.getImage());
				file1.delete();
				
				
				
				//update new photo
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				booking.setImage(file.getOriginalFilename());
				
			}else
			{
				booking.setImage(oldbookingDetail.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			booking.setUser(user);
			this.bookingRepository.save(booking); 
			session.setAttribute("message",new Message("Booking Updated Successfully ...!!","success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Booking Name"+ booking.getCustomername());
        System.out.println("Booking Id"+ booking.getBid());
		return  "redirect:/user/booking/"+booking.getBid();
		
	}
	
	//Your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model model){
		model.addAttribute("title","Your Profile");
		
		
		
		return "/normal/profile";
		
	}
	
	//Open Setting Handler
	@GetMapping("/setting")
	public String openSetting() {
		return "normal/setting";
	}
	
	
	
	//Change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session) {
		System.out.println("Old Password"+ oldPassword);
		System.out.println("New Password"+ newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		
		System.out.println(currentUser.getPassword());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) 
		{
			//change
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message",new Message("Your Password Changed Successfully!!...", "alert-success"));
		}else if((this.bCryptPasswordEncoder.matches(oldPassword, currentUser.setPassword(newPassword))))
		{
			session.setAttribute("message",new Message("Same Old and New Password!!...", "alert-error"));
			return "redirect:/user/setting";
		}else
		{
			
			session.setAttribute("message",new Message("Wrong Old Password!!...", "alert-error"));
			return "redirect:/user/setting";
		}
		return "redirect:/user/index";
		
	}
	
	
	
	

}
