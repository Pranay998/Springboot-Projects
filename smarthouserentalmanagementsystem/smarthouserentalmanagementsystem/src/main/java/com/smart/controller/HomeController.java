package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	

	
	
	@RequestMapping("/")
	public String home(Model model) {
		
	model.addAttribute("title", "Home- Rental Management System");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
	model.addAttribute("title", "About- Rental Management System");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		
	model.addAttribute("title", "Register- Rental Management System");
	model.addAttribute("user",new User());
		return "signup";
	}
	
	//handler for registring user
	@RequestMapping(value="/do_register",method = {RequestMethod.POST, RequestMethod.GET})
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result1,@RequestParam(value="agreement",defaultValue="false")boolean agreement,Model model,HttpSession session)
	{
	try {
		if(!agreement) {
			System.out.println("You have not agreed the conditions");
			throw new Exception("You have not agreed the conditions");
		}
		if(result1.hasErrors())
		{
			System.out.println("ERROR"+ result1.toString());
			model.addAttribute("user", user);
			return "signup";
		}
		user.setRole("ROLE_USER");
		user.setEnabled(true);		
		user.setImageUrl("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		System.out.println("Agreement" + agreement);
		System.out.println("USER" + user);
		User result = this.userRepository.save(user);
		model.addAttribute("user", new User());
		session.setAttribute("message", new Message("Successfully Registered!!" ,"alert-success"));
		return "signup";
		
	} catch (Exception e) {
		e.printStackTrace();
		model.addAttribute("user", user);
		session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(),"alert-danger"));
		return "signup";

	}
			}

	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login Page");
		return "login";
		
	}

}