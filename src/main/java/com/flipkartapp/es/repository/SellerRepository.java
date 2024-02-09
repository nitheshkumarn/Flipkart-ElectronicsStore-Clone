package com.flipkartapp.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.Seller;


public interface SellerRepository extends JpaRepository<Seller, Integer>{ 

}
