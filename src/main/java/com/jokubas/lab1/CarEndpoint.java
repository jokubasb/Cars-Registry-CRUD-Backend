package com.jokubas.lab1;

import com.baeldung.springsoap.gen.GetCarsResponse;
import com.jokubas.lab1.repository.CarRepository;
import com.jokubas.lab1.model.Car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.baeldung.springsoap.gen.GetCarsRequest;
import com.baeldung.springsoap.gen.GetCarsResponse;
import com.baeldung.springsoap.gen.ObjectFactory;

@Endpoint
public class CarEndpoint {
	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

	private CarRepository cr;

	@Autowired
	public CarEndpoint(CarRepository cr) {
		this.cr = cr;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCarsResponse")
	@ResponsePayload
	public GetCarsResponse getCar(@RequestPayload GetCarsRequest request) {
		GetCarsResponse response = new GetCarsResponse();
		Car car = cr.findById(request.getId());
		response.setCar(cr.findById(request.getId()));

		return response;
	}
}