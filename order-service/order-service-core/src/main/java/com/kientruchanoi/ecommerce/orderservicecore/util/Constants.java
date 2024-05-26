package com.kientruchanoi.ecommerce.orderservicecore.util;

public class Constants {

    public static String USER_ROLE = "USER";
    public static String MOD_ROLE = "MODERATOR";
    public static String ADMIN_ROLE = "ADMIN";
    public static String ROOT_ROLE = "ROOT";

    public static class HttpMessage {
        public static String ACCESS_DENIED = "Không được phép truy cập.";
    }
    public static class OrderStatus {
        public static String ORDER_NOT_FOUND = "Không tìm thấy đơn hàng.";
        public static String ORDER_CREATE_SUCCESS = "Tạo đơn hàng thành công.";
        public static String ORDER_CANCEL_FAILED = "Không thể huỷ đơn.";
        public static String ORDER_CANCEL_SUCCESS = "Đã huỷ đơn hàng.";
        public static String ORDER_CANNOT_ACCEPT = "Không thể nhận đơn hàng do không trong trạng thái chờ.";
        public static String ORDER_ACCEPTED = "Đã tiếp nhận đơn hàng";
        public static String ORDER_CANNOT_REJECT = "Không thể từ chối đơn hàng do không trong trạng thái chờ.";
        public static String ORDER_REJECTED = "Đã từ chối đơn hàng";
        public static String ORDER_CANNOT_DELIVERING = "Tiếp nhận đơn hàng trước khi giao";
        public static String ORDER_CANNOT_RECEIVE = "Đơn hàng không trong trạng thái đang giao";
    }

    public static class FirebaseData {
        public static String TYPE = "type";
        public static String TITLE = "title";
        public static String BODY = "body";
    }
}