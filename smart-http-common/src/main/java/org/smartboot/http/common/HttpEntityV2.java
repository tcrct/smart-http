/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: HttpV2Entity.java
 * Date: 2018-01-23
 * Author: sandao
 */

package org.smartboot.http.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.smartboot.http.common.enums.MethodEnum;
import org.smartboot.http.common.utils.HttpHeaderConstant;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http消息体，兼容请求与响应
 *
 * @author 三刀 2018/06/02
 */
public class HttpEntityV2 {
    protected final BufferRange methodRange = new BufferRange();
    protected final BufferRange uriRange = new BufferRange();
    protected final BufferRange protocolRange = new BufferRange();
    protected final BufferRanges headerRanges = new BufferRanges();
    protected int initPosition = 0;
    State state = State.method;
    ByteBuffer buffer;
    private FixedLengthFrameDecoder bodyForm;
    private int currentPosition = 0;

    private MethodEnum methodEnum;

    private String uri;
    private String protocol;
    private String contentType;
    private int contentLength;

    private Map<String, String> headMap = new HashMap<>();


    public void decodeHead() {
        getMethodRange();
        uri = get(uriRange);
        protocol = get(protocolRange);
        for (BufferRange bufferRange : headerRanges.headers) {
            if (!bufferRange.isOk || bufferRange.isMatching) {
                continue;
            }
            String headStr = get(bufferRange);
            headMap.put(StringUtils.substringBefore(headStr, ":"), StringUtils.substringAfter(headStr, ":").trim());
        }
    }

    public void rest() {
        methodRange.reset();
        uriRange.reset();
        protocolRange.reset();
        headerRanges.reset();
        buffer = null;
        initPosition = 0;
        setCurrentPosition(0);
        state = State.method;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public byte[] getBytes(BufferRange range) {
        return getBytes(range, 0);
    }

    public byte[] getBytes(BufferRange range, int offset) {
        int p = buffer.position();
        byte[] b = new byte[range.length - offset];
        buffer.position(range.start + offset);
        buffer.get(b);
        buffer.position(p);
        return b;
    }

    public String get(BufferRange range) {
        return new String(getBytes(range));
    }

    public MethodEnum getMethodRange() {
        return methodEnum == null ? methodEnum = MethodEnum.getByMethod(buffer.array(), methodRange.start, methodRange.length)
                : methodEnum;
    }

    public String getContentType() {
        if (contentType != null) {
            return contentType;
        }
        contentType = headMap.get(HttpHeaderConstant.Names.CONTENT_TYPE);
        if (contentType != null) {
            return contentType;
        }
        BufferRange bufferRange = getAndRemove(HttpHeaderConstant.HeaderBytes.CONTENT_TYPE);
        if (bufferRange != null) {
            contentType = new String(getBytes(bufferRange, HttpHeaderConstant.HeaderBytes.CONTENT_TYPE.length + 1)).trim();
            headMap.put(HttpHeaderConstant.Names.CONTENT_TYPE, contentType);
        }
        return contentType;
    }

    public int getContentLength() {
        if (contentLength > 0) {
            return contentLength;
        }
        if (headMap.containsKey(HttpHeaderConstant.Names.CONTENT_LENGTH)) {
            contentLength = NumberUtils.toInt(headMap.get(HttpHeaderConstant.Names.CONTENT_LENGTH));
            return contentLength;
        }
        BufferRange bufferRange = getAndRemove(HttpHeaderConstant.HeaderBytes.CONTENT_LENGTH);
        if (bufferRange != null) {
            contentLength = NumberUtils.toInt(new String(getBytes(bufferRange, HttpHeaderConstant.HeaderBytes.CONTENT_LENGTH.length + 1)).trim());
            headMap.put(HttpHeaderConstant.Names.CONTENT_LENGTH, contentType);
        }
        return contentLength;
    }

    public BufferRange getAndRemove(byte[] contentType) {
        final List<BufferRange> headers = headerRanges.headers;
        for (BufferRange bufferRange : headers) {
            if (!bufferRange.isOk || bufferRange.length < contentType.length || bufferRange.isMatching) {
                continue;
            }
            int pos = bufferRange.start;
            boolean ok = true;
            for (byte b : contentType) {
                if (b != buffer.get(pos++)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                bufferRange.isMatching = true;
                return bufferRange;
            }

        }
        return null;
    }

    public FixedLengthFrameDecoder getBodyForm() {
        return bodyForm;
    }

    public void setBodyForm(FixedLengthFrameDecoder bodyForm) {
        this.bodyForm = bodyForm;
    }
}
