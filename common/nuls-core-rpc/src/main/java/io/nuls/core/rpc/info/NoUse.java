package io.nuls.core.rpc.info;

import io.nuls.core.core.ioc.SpringLiteContext;
import io.nuls.core.rpc.model.ModuleE;
import io.nuls.core.rpc.netty.bootstrap.NettyServer;
import io.nuls.core.rpc.netty.channel.manager.ConnectManager;
import io.nuls.core.rpc.netty.processor.ResponseMessageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 用于测试
 *
 * @author tangyi
 * @date 2018/12/3
 */
public class NoUse {

    /**
     * 模拟核心模块（Manager），测试专用
     * For internal debugging only
     * Simulate a kernel module
     */
    public static int mockKernel() throws Exception {
        SpringLiteContext.init("io.nuls.core.rpc.cmd.kernel");
        return startKernel();
    }

    public static void startKernel(String host, int port, String path) throws Exception {
        NettyServer.startServer(port, host, path);
        // Start server instance
        ConnectManager.LOCAL.setMethods(new ArrayList<>());
        ConnectManager.LOCAL.setAbbreviation(ModuleE.KE.abbr);
        ConnectManager.LOCAL.setModuleName(ModuleE.KE.name);
        ConnectManager.LOCAL.setModuleDomain(ModuleE.KE.domain);
        Map<String, String> connectionInformation = new HashMap<>(12);
        connectionInformation.put(Constants.KEY_IP, host);
        connectionInformation.put(Constants.KEY_PORT, port + "");
        ConnectManager.LOCAL.setConnectionInformation(connectionInformation);
        ConnectManager.startService = true;
        ConnectManager.scanPackage(Set.of("io.nuls.core.rpc.cmd.kernel"));
        ConnectManager.ROLE_MAP.put(ModuleE.KE.abbr, connectionInformation);
        ConnectManager.updateStatus();
    }

    public static int startKernel() throws Exception {
        return startKernel(8771);
    }

    public static int startKernel(int port) throws Exception {
        startKernel(HostInfo.getLocalIP(), port, "");
        return port;
    }

    /**
     * 模拟启动模块，单元测试专用
     * Analog Startup Module, Unit Test Specific
     */
    public static void mockModule() throws Exception {
        mockModule(8771);
    }

    /**
     * 模拟启动模块，单元测试专用
     * Analog Startup Module, Unit Test Specific
     */
    public static void mockModule(int port) throws Exception {
        NettyServer.getInstance("test", "TestModule", "test.com")
                .moduleRoles("test_role", new String[]{"1.0"})
                .moduleVersion("1.0");

        ConnectManager.getConnectByUrl("ws://" + HostInfo.getLocalIP() + ":" + port, Constants.THREAD_POOL_SIZE);
        // Get information from kernel
        ResponseMessageProcessor.syncKernel("ws://" + HostInfo.getLocalIP() + ":" + port);
    }
}
