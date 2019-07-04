package com.alibaba.jvm.sandbox.core;

import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.lang3.StringUtils;

import static com.alibaba.jvm.sandbox.core.util.SandboxStringUtils.getCauseMessage;

/**
 * 沙箱内核启动器
 * Created by luanjia@taobao.com on 16/10/2.
 */
public class CoreLauncher {


    /**
     * 核心启动器
     * @param targetJvmPid 目标应用的pid
     * @param agentJarPath sandbox-agent的路径，"xxx/sandbox-agent.jar"
     * @param token token其他信息，主要是其他配置信息，以前只有一个token
     * @throws Exception
     */
    public CoreLauncher(final String targetJvmPid, final String agentJarPath, final String token) throws Exception {
        attachAgent(targetJvmPid, agentJarPath, token);// 加载agent
    }

    /**
     * 内核启动程序
     *
     * @param args 参数
     *             [0] : PID
     *             [1] : agent.jar's value
     *             [2] : token
     */
    public static void main(String[] args) {
        try {

            //校验参数
            if (args.length != 3 || StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1]) || StringUtils.isBlank(args[2])) {
                throw new IllegalArgumentException("illegal args");
            }

            new CoreLauncher(args[0], args[1], args[2]);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.err.println("sandbox load jvm failed : " + getCauseMessage(t));
            System.exit(-1);
        }
    }


    /**
     * 加载Agent
     * @param targetJvmPid 目标java的pid
     * @param agentJarPath 目标路径 sandbox-agent
     * @param cfg 配置信息
     * @throws Exception
     */
    private void attachAgent(final String targetJvmPid, final String agentJarPath, final String cfg) throws Exception {
        VirtualMachine vmObj = null;
        try {
            vmObj = VirtualMachine.attach(targetJvmPid);//attach目标进程
            if (vmObj != null) {
                vmObj.loadAgent(agentJarPath, cfg); //loadAgent,这个agent就是sandbox-agent.jar。cfg为相关配置
            }
        } finally {
            if (null != vmObj) {
                vmObj.detach();
            }
        }

    }

}
