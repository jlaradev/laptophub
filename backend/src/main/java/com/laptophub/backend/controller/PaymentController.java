package com.laptophub.backend.controller;

import com.laptophub.backend.model.Payment;
import com.laptophub.backend.model.PaymentStatus;
import com.laptophub.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public Payment findById(@PathVariable Long paymentId) {
        return paymentService.findById(paymentId);
    }

    @GetMapping("/stripe/{stripePaymentId}")
    public Payment findByStripeId(@PathVariable String stripePaymentId) {
        return paymentService.findByStripePaymentId(stripePaymentId);
    }

    @PutMapping("/{paymentId}/status/{estado}")
    public Payment updateStatus(
            @PathVariable Long paymentId,
            @PathVariable PaymentStatus estado
    ) {
        return paymentService.updatePaymentStatus(paymentId, estado);
    }

    @PutMapping("/{paymentId}/stripe-id")
    public Payment setStripeId(
            @PathVariable Long paymentId,
            @RequestParam String value
    ) {
        return paymentService.setStripePaymentId(paymentId, value);
    }

    @PostMapping("/{paymentId}/simulate")
    public Payment simulate(
            @PathVariable Long paymentId,
            @RequestParam boolean success
    ) {
        return paymentService.processPaymentSimulated(paymentId, success);
    }
}