package com.dscommerce.messaging;

public final class RabbitMQConstants {

    public static final String EXG_ORDER_TOPIC = "order.topic";
    public static final String QUEUE_ORDER_CONFIRMATION = "email.order-confirmation";
    public static final String RK_ORDER_CREATED = "order.created";
    public static final String DLX = "order.dlx";
    public static final String DLQ = "order.dlq";
    public static final String RK_DL = "fail";

    private RabbitMQConstants() {
    }
}
