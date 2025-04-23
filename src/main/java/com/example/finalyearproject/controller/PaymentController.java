package com.example.finalyearproject.controller;

import com.example.finalyearproject.service.StripeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.stripe.model.checkout.Session;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-session") //api endpoint to create a card payment session
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> payload) {
        try {
            //extract amount and username from the payload
            double amount = Double.parseDouble(payload.get("amount").toString());
            String username = payload.get("username").toString();

            //create a Stripe checkout session through the service class, using amount and username
            Session session = stripeService.createCheckoutSession(amount, username);

            //format the response as a hashmap
            Map<String, String> response = new HashMap<>();
            response.put("id", session.getId());
            response.put("url", session.getUrl()); //store the session URL so we can redirect to it

            return ResponseEntity.ok(response);  //return the response
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //controller method to handle payment success redirect
    @GetMapping("/payment/success")
    public RedirectView paymentSuccess(HttpServletRequest request) {

        return new RedirectView("/main?payment=success"); //redirect to the success URL
    }

    //controller method to handle payment failure or cancellation
    @GetMapping("/payment/cancel")
    public RedirectView paymentCancelled(HttpServletRequest request) {
        return new RedirectView("/main?payment=cancel"); //redirect to the cancel URL
    }

}
