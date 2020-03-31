/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Http11Request.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.enums.HttpMethodEnum;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.utils.EmptyInputStream;
import org.smartboot.http.utils.PostInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
public final class Http11Request extends AbstractHttpRequest {

    private InputStream inputStream;


    private String formUrlencoded;

    private boolean paramDecoded = false;

    Http11Request(BaseHttpRequest request) {
        init(request, new Http11Response(this, request.getAioSession().writeBuffer()));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        if (!HttpMethodEnum.POST.getMethod().equalsIgnoreCase(getMethod())) {
            inputStream = new EmptyInputStream();
        } else if (formUrlencoded == null) {
            inputStream = new PostInputStream(request.getAioSession().getInputStream(getContentLength()), getContentLength());
        } else {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return inputStream;
    }


    @Override
    public String[] getParameterValues(String name) {
        Map<String, String[]> parameters = request.getParameters();

        if (!paramDecoded && formUrlencoded != null) {
            request.decodeParamString(formUrlencoded, parameters);
        }
        return parameters.get(name);
    }

    public void reset() {
        super.reset();
        formUrlencoded = null;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
    }

}
