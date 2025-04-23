package com.example.finalyearproject.service;

import com.example.finalyearproject.model.User;
import com.example.finalyearproject.repository.UserRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Autowired
    UserRepo userRepo;

    //get the Stripe secret key
    @Value("${stripe.api.key}")
    private String secretKey;

    //method to create the Stripe card payment (checkout) session
    public Session createCheckoutSession(double amount, String username) throws StripeException {
        Stripe.apiKey = secretKey; //set the key

        //retrieve the user's email from the database
        String customerEmail = userRepo.findByUsername(username)
                .map(User::getEmail)
                .filter(this::isValidEmail) //check its a valid email address, using the helper method
                .orElse("guest@virtualvendingmachine.com");  //if invalid or null, fallback to this default email (primarily for guests)

        //initialise the payment form
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/payment/payment/success") //define success URL
                .setCancelUrl("http://localhost:8080/api/payment/payment/cancel")   //define the cancel/failure URL
                .setCustomerEmail(customerEmail)  //pre-fill the user's email in the payment form
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("gbp") //set currency as £
                                                .setUnitAmount((long) (amount * 100)) //convert to pence (required for Stripe)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Virtual Vending Machine Purchase") //set the name for the form
                                                                .build())
                                                .build())
                                .build())
                .build();

        return Session.create(params);  //return the created session
    }

    //helper method to check if the email is a valid email address
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"); //use regex to check the structure of the email
    }
}
