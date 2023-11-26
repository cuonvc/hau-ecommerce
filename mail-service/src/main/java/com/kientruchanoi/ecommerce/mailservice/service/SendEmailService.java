package com.kientruchanoi.ecommerce.mailservice.service;


import com.kientruchanoi.ecommerce.mailservice.payload.EmailTo;

public interface SendEmailService {
    void send(EmailTo emailTo);
}
