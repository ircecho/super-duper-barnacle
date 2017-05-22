package com.example.demo;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "/v2/admin/payment")
public class AdminPaymentController  {
    @RequestMapping(value = "paymentStatus", method = RequestMethod.PUT)
    public String updatePaymentStatus(@RequestParam("id") Payment payment) {
        return null;
    }
}
