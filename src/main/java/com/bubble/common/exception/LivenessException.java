package com.bubble.common.exception;

/**
 * @author wugang
 * date: 2020-08-18 14:42
 **/
public class LivenessException extends RuntimeException {
    private static final long serialVersionUID = -294721021476520131L;

    public LivenessException(String msg) {
        super(msg);
    }

}
