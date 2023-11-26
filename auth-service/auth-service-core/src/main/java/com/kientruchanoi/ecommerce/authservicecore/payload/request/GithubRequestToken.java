package com.kientruchanoi.ecommerce.authservicecore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubRequestToken {
    private String clientId;
    private String clientSecret;
    private String code;
}
