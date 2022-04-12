package com.capgemini.demo.carrental.model;

import com.capgemini.carrental.dto.response.CarResponse;
import com.capgemini.carrental.dto.response.TenantResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
public class Rental {

    private Long id;
    private TenantResponse tenant;
    private List<CarResponse> rentedCars;
    private LocalDate beginningOfRental;
    private LocalDate endOfRental;
}
