package com.xpresPayment.Airtimeservice;

import com.xpresPayment.Airtimeservice.dto.request.RegRequest;
import com.xpresPayment.Airtimeservice.dto.response.ProductDto;
import com.xpresPayment.Airtimeservice.dto.response.RegisterResponse;
import com.xpresPayment.Airtimeservice.dto.response.ResponseDto;
import com.xpresPayment.Airtimeservice.enums.TransactionStatus;
import com.xpresPayment.Airtimeservice.model.Invoice;
import com.xpresPayment.Airtimeservice.repository.InvoiceRepository;
import com.xpresPayment.Airtimeservice.service.serviceImpl.AirtimeServiceImpl;
import com.xpresPayment.Airtimeservice.service.serviceImpl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;


@SpringBootTest
@Testcontainers
@Slf4j
class AirtimeServiceApplicationTests {

	@Autowired
	private AuthServiceImpl authServiceImpl;
	@Autowired
	private InvoiceRepository invoiceRepository;
	@Autowired
	private AirtimeServiceImpl airtimeServiceImpl;

	@Container
	final static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

	@DynamicPropertySource
	static void setProperty(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url",postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username",postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password",postgreSQLContainer::getPassword);
	}

	@Test
	void shouldRegisterNewUserAndReturnSuccess() {
		RegisterResponse response= authServiceImpl.getRegisterResponse(registrationRequest());
		log.info(response.toString());
		Assertions.assertEquals("SUCCESS",response.getStatus());
	}

	@Test
	void shouldPurchaseAirtimeAndReturnSuccessStatus(){

		ResponseDto response = airtimeServiceImpl.purchaseAirtime(getInvoice());
		log.info(response.toString());
		Assertions.assertEquals("SUCCESS",response.getStatus());
	}

	@Test
	void shouldNotPurchaseAirtimeAndReturnFailedStatus(){
		Invoice invoice = getInvoice();
		invoice.setPhoneNumber("08136015875");
		ResponseDto response = airtimeServiceImpl.purchaseAirtime(invoice);
		log.info(response.toString());
		Assertions.assertEquals("FAILED",response.getStatus());
	}

	@Test
	void shouldReturnAllAirtimeProducts(){
		List<ProductDto> airtimeProduct = airtimeServiceImpl.getAllAirtimeProduct();
		Assertions.assertNotNull(airtimeProduct.get(0));
	}

	private Invoice getInvoice(){
		Invoice invoice = new Invoice();
		invoice.setAirtimeAmount("1000");
		invoice.setNetworkCode("MTN_24207");
		invoice.setPhoneNumber("08033333333");
		invoice.setTransactionStatus(TransactionStatus.PENDING);
		invoice.setUniqueId(UUID.randomUUID().toString());
		return invoiceRepository.save(invoice);
	}

	private RegRequest registrationRequest() {
		RegRequest request = new RegRequest();
		request.setEmail("ogwuchechris@gmail.com");
		request.setPassword("Ogwuche@21");
		request.setFull_name("Chris Ogwuche");
		request.setConfirm_password("Ogwuche@21");
		request.setPhone_number("08136015875");
		return request;
	}

}
