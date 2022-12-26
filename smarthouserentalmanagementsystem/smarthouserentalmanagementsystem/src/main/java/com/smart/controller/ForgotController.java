package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	Random random = new Random(1000);
	
	@Autowired
	 private EmailService emailService;
	
	@Autowired
	 private UserRepository userRepository;
	
	@Autowired
	 private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		
		return "Forgot_email_form";
	}

	@PostMapping("/sendOtp")
	public String sendOtp(@RequestParam("email")String email,HttpSession session) {
		
		System.out.println("Email:"+ email);
		
		//generating otp of four digit
		 
		
		
		int otp = random.nextInt(999999);
		System.out.println("OTP" + otp);
		
		//code for send otp to email
		String subject = "OTP From SRM";
		String message = ""+"<div style='border:1px solid #e2e2e2; padding:20px'>"+"<h1>"+"OTP is "+"<b>"+otp+"</n>"+"<h1>"+"<div>";
		String to = email; 
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "Verify_Otp";
			
		}else
		{
			
			session.setAttribute("message", "Check your mail id..!!");
			return "Forgot_email_form";
		}
		
		
	}
	
	//verify OTP
	
	@PostMapping("/Verify_Otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		int myOtp = (int)session.getAttribute("myotp") ;
		String email = (String)session.getAttribute("email");
		if(myOtp == otp)
		{
			//password change form
			
			 User user = this.userRepository.getUserByUserName(email);
			 if(user == null)
			 {
				 //send error message
				 session.setAttribute("message", "User does not exist with this email!!");
					return "Forgot_email_form";
			 }else
			 {
				 //send change pass form  
				 
			 }
			
			return "password_change_form";
		}else
		{
			session.setAttribute("message", "Wrong OTP..!!"); 
			return "Verify_Otp";
		}
		
		
	}
	
	//change password
	
	@PostMapping("/change_password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session) {
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		this.bCryptPasswordEncoder.encode(newpassword);
		this.userRepository.save(user);
	
		return "redirect:/signin?change=password changed successfully..";
		
		
		
	}
	
}
