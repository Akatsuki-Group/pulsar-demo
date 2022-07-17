package com.itheima.admin;

import org.apache.pulsar.client.admin.PulsarAdmin;

import java.util.List;

// 演示 如何使用JAVA API 完成 名称空间操作
public class _02_createNamespace {

    public static void main(String[] args) throws Exception {

        //1. 创建Pulsar的Admin管理对象
        String serviceHttpUrl = "http://node1:8080,node2:8080,node3:8080";
        PulsarAdmin pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl(serviceHttpUrl).build();
        //2. 执行相关的操作

        //2.1 如何创建名称空间
        // pulsarAdmin.namespaces().createNamespace("itcast_pulsar_t/itcast_pulsar_n");

        //2.2 获取在某个租户下, 一共有那些名称空间:
        /*List<String> namespaces = pulsarAdmin.namespaces().getNamespaces("itcast_pulsar_t");

        for (String namespace : namespaces) {
            System.out.println(namespace);
        }*/

        //2.3: 删除名称空间
        pulsarAdmin.namespaces().deleteNamespace("itcast_pulsar_t/itcast_pulsar_n");

        //3. 关闭admin对象
        pulsarAdmin.close();
    }

}
