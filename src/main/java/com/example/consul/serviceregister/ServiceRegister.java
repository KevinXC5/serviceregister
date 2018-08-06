package com.example.consul.serviceregister;

import com.ecwid.consul.ConsulException;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * service register/deregister
 * 用于服务的注册于注销
 * <br/>
 * 使用IDE运行该程序，在关闭时，可能不会执行deregiste()注销操作。
 * 打jar包执行后，Ctrl+c 中断程序会执行deregiste()注销操作
 * @author geng_pool
 * @create 2018-08-01-19:06
 **/
@Configuration
public class ServiceRegister {
    //service name
    @Value("${service.name}")
    private String serviceName;

    //service id 同一个服务多节点部署时，区分不同节点上的服务
    @Value("${service.id}")
    private String serviceId;
    //该服务所在机器地址
    @Value("${service.ip}")
    private String serviceIp;
    //设置consul检查该服务是否健康的间隔
    @Value("${service.check.interval}")
    private String serviceCheckInterval;
    //该服务的端口
    @Value("${server.port}")
    private String port;

    //consul的agent 连接consul集群的某一个agent即可，需consul管理员提供地址
    private ConsulClient client = new ConsulClient("172.28.10.20");
    //本服务
    private NewService service;
    //标注是否注册成功
    private boolean isRegisted = false;

    /**
     * 注册服务。
     * 该类构造方法结束后立即执行该方法，只执行一次，用于注册服务
     */
    @PostConstruct
    private void registe() {
        System.out.println("开始注册服务" + "");

        //服务的信息
        service = new NewService();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setAddress(serviceIp);
        service.setPort(Integer.valueOf(port));
        //服务的健康检查策略 注册服务时，就可以同时在服务中写上健康检查策略注册到consul
        NewService.Check check = new NewService.Check();
        check.setHttp("http://" + serviceIp + ":" + port + "/health");//需要您提供一个接口给consul，用于定时检查您应用的健康状况
        check.setInterval(serviceCheckInterval);//设置consul检查健康状况的间隔
        //说明，这里是根据http接口来检查服务的健康状况，我们的<服务接入文档>推荐的也是该方式
        //将check添加入service
        service.setCheck(check);

        //将service注册到consul
        try {
            client.agentServiceRegister(this.service);//若连接consul不成功，会报运行时异常
        } catch (ConsulException ex) {
            System.out.println("注册失败，请检查consul地址是否正确，或联系consul管理员" + "");
            isRegisted = false;
            return;
        }
        isRegisted = true;//注册成功
        System.out.println("服务注册结束" + "");

    }

    /**
     * 注销服务。
     * 销毁该类前自动调用，注销registe()方法中注册的服务，若为注册成功，则不注销
     */
    @PreDestroy
    private void deregiste() {
        if (isRegisted) {
            System.out.println("开始注销服务" + "");
            try {
                client.agentServiceDeregister(serviceId);
            } catch (ConsulException ex) {
                System.out.println("服务注销操作失败，请联系consul管理员手动注销" + "");
                return;
            }
            System.out.println("服务已经注销" + "");
        } else {
            System.out.println("该服务未注册成功，不执行注销操作" + "");
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }
}
