package com.gradlespringboot.razorpay.controller;

import com.gradlespringboot.razorpay.entities.Payment;
import com.gradlespringboot.razorpay.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/payment")
    public String showPaymentPage() {
        return "payment"; // "payment" is the name of your HTML file
    }

    @RequestMapping("/payment")
    @ResponseBody
    public Payment processPayment(@RequestParam Map<String, Object> data) throws Exception {
        System.out.println(data);
        Object amountObj = data.get("amount");
        if (amountObj == null) {
            throw new Exception("Amount is mandatory");
        }
        float amt = Float.parseFloat(amountObj.toString());
        Order order = null;
        try {
            RazorpayClient rj = new RazorpayClient("rzp_test_QvfhRP9slPcend", "mhCcqqxaDZ3wP3CKzBCC5XuB");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amt * 100)); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");

            //creating new PaymentEntity
            order = rj.orders.create(orderRequest);
            System.out.println(order);

            //save payments in database
            Payment paymentEntity = new Payment();

            paymentEntity.setOrderId(order.get("id") != null ? order.get("id").toString() : null);
            paymentEntity.setAmount(order.get("amount") != null ? amt : null); // convert to rupees
            paymentEntity.setCurrency(order.get("currency") != null ? order.get("currency").toString() : null);
            paymentEntity.setMode(order.get("method") != null ? order.get("method").toString() : null);
            paymentEntity.setStatus(order.get("status") != null ? order.get("status").toString() : null);
            paymentEntity.setCreatedAt(LocalDateTime.now()); // Store payment time
            Payment savepay = this.paymentRepository.save(paymentEntity);
            return savepay;

        } catch (Exception e) {
            System.out.println("Order generation error: " + e.getMessage());
            throw e;
        }
    }

    @RequestMapping("/payment/{id}")
    @ResponseBody
    public Payment payment(@PathVariable("id") Long Id) {
        System.out.println(Id);
        Payment paymentEntity = this.paymentRepository.findById(Id).orElse(null);
        System.out.println(paymentEntity);
        return paymentEntity;
    }

    @PostMapping("/payment/success/{id}")
    @ResponseBody
    public Payment success(@PathVariable("id") Long Id) {
        System.out.println(Id);
        Payment paymentEntity = this.paymentRepository.findById(Id).orElse(null);

        if (paymentEntity != null) {
            paymentEntity.setStatus("paid");
            paymentRepository.save(paymentEntity);
        }
        return paymentEntity;
    }

    @PostMapping("/payment/fail/{id}")
    @ResponseBody
    public Payment fail(@PathVariable("id") Long Id) {
        System.out.println(Id);
        Payment paymentEntity = this.paymentRepository.findById(Id).orElse(null);

        if (paymentEntity != null) {
            paymentEntity.setStatus("failed");
            paymentRepository.save(paymentEntity);
        }
        return paymentEntity;
    }

}
