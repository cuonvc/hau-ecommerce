package com.kientruchanoi.hauecommerce.service;

import com.kientruchanoi.hauecommerce.payload.EmailTo;
import org.springframework.stereotype.Service;

public interface EmailService {

    public void send(EmailTo email);
}
