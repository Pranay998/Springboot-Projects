package com.smart.dao;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.entities.Booking;
import com.smart.entities.User;



@Repository
public interface BookingRepository extends JpaRepository<Booking,Integer> {
	//Pagination
	
	@Query("from Booking as c where  c.user.id=:userId")
	public Page<Booking> findBookingByUser(@Param("userId")int userId,Pageable pePageable); 
	
	//search

//	public List<Booking>findByUserNameAndUser(String customername,User user);
	
	
	 @Query(value="SELECT * FROM Booking b  WHERE b.customername  LIKE %:query%",nativeQuery = true)
	    List<Booking> searchBooking(@Param("query")String query);

	
}
