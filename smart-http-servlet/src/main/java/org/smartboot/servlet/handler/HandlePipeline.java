package org.smartboot.servlet.handler;

import org.smartboot.servlet.HandlerContext;
import org.smartboot.servlet.Pipeline;

/**
 * @author 三刀
 * @version V1.0 , 2019/11/3
 */
public final class HandlePipeline extends Handler implements Pipeline {
    /**
     * 管道尾
     */
    private Handler tailHandle;

    /**
     * 添加HttpHandle至末尾
     *
     * @param handle 尾部handle
     * @return 当前管道对象
     */
    public Pipeline next(Handler handle) {
        if (nextHandle == null) {
            nextHandle = tailHandle = handle;
            return this;
        }
        Handler httpHandle = tailHandle;
        while (httpHandle.nextHandle != null) {
            httpHandle = httpHandle.nextHandle;
        }
        httpHandle.nextHandle = handle;
        return this;
    }

    @Override
    public void handleRequest(HandlerContext handlerContext) throws Exception {
        nextHandle.handleRequest(handlerContext);
    }
}