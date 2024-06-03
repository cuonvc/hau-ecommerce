package com.kientruchanoi.ecommerce.payment_gateway.utils;

public class Constant {
    public static class ZaloConfig {
        public static final String ZP_APPID = "2554"; //public on github
        public static final String ZP_KEY1 = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn"; //public on github
        public static final String ZP_KEY2 = "trMrHtvjo6myautxDUiAcYsVtaeQ8nhf"; //public on github
        public static final String ZP_GET_BANK_ENDPOINT = "https://sbgateway.zalopay.vn/api/getlistmerchantbanks";
        public static final String ZP_CREATE_ORDER_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";
    }
}
