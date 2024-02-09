package com.flipkartapp.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{ 

}
