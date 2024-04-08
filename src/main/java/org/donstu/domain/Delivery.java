package org.donstu.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Delivery {
    private String deliveryId;
    private String orderId;
    private String deliveryAddress;
    private Date expectedDeliveryDate;
    private String deliveryStatus;
}