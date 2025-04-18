package com.serhat.ecommerce.notification.notificatioException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoNotificationsFoundException extends RuntimeException
{
    public NoNotificationsFoundException(String s) {
        super(s);
    }
}
