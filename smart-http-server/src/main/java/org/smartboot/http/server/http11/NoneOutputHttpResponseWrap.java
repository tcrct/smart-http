/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: NoneOutputHttpResponseWrap.java
 * Date: 2018-02-17
 * Author: sandao
 */

package org.smartboot.http.server.http11;

import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/8
 */
class NoneOutputHttpResponseWrap implements HttpResponse {
    private HttpResponse response;

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return response.getHttpStatus();
    }

    @Override
    public void setHttpStatus(HttpStatus httpStatus) {
        response.setHttpStatus(httpStatus);
    }

    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public String getHeader(String name) {
        return response.getHeader(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return response.getHeaders();
    }

    @Override
    public void write(ByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }
}
