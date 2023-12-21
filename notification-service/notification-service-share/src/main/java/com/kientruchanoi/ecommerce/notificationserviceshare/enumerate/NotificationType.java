package com.kientruchanoi.ecommerce.notificationserviceshare.enumerate;

public enum NotificationType {
    USER_CREATED("Có người đăng ký mới"),
    ORDER_CREATED("Có đơn hàng mới"),
    ORDER_CANCEL("Đơn hàng bị huỷ"),
    ORDER_ACCEPTED("Đơn hàng đã được tếp nhận"),
    ORDER_DELIVERY("Đơn hàng đang trên đường giao tới bạn"),
    ORDER_REJECTED("Đơn hàng bị từ chối"),
    ORDER_DONE("Hoàn thành đơn hàng"),
    WALLET_REQUIRE_DEPOSIT("Yêu cầu nạp tiền"),
    WALLET_ACCEPTED_DEPOSIT("Tiếp nhận yêu cầu nạp tiền"),
    WALLET_REQUIRE_WITHDRAW("Yêu cầu rút tiền"),
    WALLET_ACCEPTED_WITHDRAW("Tiếp nhận yêu cầu rút tiền"),
    WALLET_REFUND("Hoàn tiền đơn hàng");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
