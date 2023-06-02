/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2023 double joker （262610965@qq.com） . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iohao.game.example.meter.login;


import com.iohao.game.bolt.broker.core.common.IoGameGlobalConfig;
import com.iohao.game.common.kit.ExecutorKit;
import com.iohao.game.example.common.cmd.MeterLoginCmd;
import com.iohao.game.example.meter.login.server.MeterLoginAction;
import com.iohao.game.example.meter.login.server.MeterLoginLogicServer;
import com.iohao.game.external.core.ExternalServer;
import com.iohao.game.external.core.config.ExternalGlobalConfig;
import com.iohao.game.external.core.config.ExternalJoinEnum;
import com.iohao.game.external.core.hook.AccessAuthenticationHook;
import com.iohao.game.external.core.netty.DefaultExternalServer;
import com.iohao.game.external.core.netty.simple.NettyRunOne;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 渔民小镇
 * @date 2022-10-08
 */
@Slf4j
public class MeterLoginTcpSocketApplication {
    public static void main(String[] args) {
        IoGameGlobalConfig.timeoutMillis = 5 * 1000;

        ExternalServer externalServer = getExternalServer();

        // 简单的启动器
        new NettyRunOne()
                // 游戏对外服
                .setExternalServer(externalServer)
                // 游戏逻辑服列表
                .setLogicServerList(List.of(new MeterLoginLogicServer()))
                // 启动 游戏对外服、游戏网关服、游戏逻辑服
                .startup();

        scheduled();
    }

    private static void scheduled() {
        ExecutorKit.newSingleScheduled("meter login")
                .scheduleAtFixedRate(() -> {
                    System.out.println();
                    log.info("MeterLoginAction.longAdder : {}", MeterLoginAction.longAdder);
                    log.info("MeterLoginAction.loginLongAdder : {}", MeterLoginAction.loginLongAdder);
                }, 1, 5, TimeUnit.SECONDS);
    }

    private static ExternalServer getExternalServer() {
        // 游戏对外服端口
        int port = 10100;

        AccessAuthenticationHook accessAuthenticationHook = ExternalGlobalConfig.accessAuthenticationHook;
        accessAuthenticationHook.setVerifyIdentity(true);
        accessAuthenticationHook.addIgnoreAuthCmd(MeterLoginCmd.cmd, MeterLoginCmd.login);

        return DefaultExternalServer.newBuilder(port)
                .externalJoinEnum(ExternalJoinEnum.TCP)
                .build();
    }
}
