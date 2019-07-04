package com.alibaba.jvm.sandbox.core.server;

import com.alibaba.jvm.sandbox.core.CoreConfigure;
import com.alibaba.jvm.sandbox.core.server.jetty.JettyCoreServer;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;

/**
 * 代理服务，位于corejar中，我们用为宿主的应用程序进行构建一个服务用于暴露网络通信，如果已经绑定了，我们不再绑定，
 * 我们通过绑定实现网络的暴露
 */
public class ProxyCoreServer implements CoreServer {

    //
    private final static Class<? extends CoreServer> classOfCoreServerImpl = JettyCoreServer.class;

    private final CoreServer proxy;

    private ProxyCoreServer(CoreServer proxy) {
        this.proxy = proxy;
    }


    @Override
    public boolean isBind() {
        return proxy.isBind();
    }

    @Override
    public void unbind() throws IOException {
        proxy.unbind();
    }

    @Override
    public InetSocketAddress getLocal() throws IOException {
        return proxy.getLocal();
    }

    @Override
    public void bind(CoreConfigure cfg, Instrumentation inst) throws IOException {
        proxy.bind(cfg, inst);
    }

    @Override
    public void destroy() {
        proxy.destroy();
    }

    @Override
    public String toString() {
        return "proxy:" + proxy.toString();
    }

    /**
     * 构建一个classLoader中的CoreServer
     * @return
     */
    public static CoreServer getInstance() {
        try {
            return new ProxyCoreServer((CoreServer) classOfCoreServerImpl.getMethod("getInstance").invoke(null));
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }
    }

}
