/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.yangbajing.portal.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/messages")
public class MessagesController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping(path = "/members/{serviceId}")
    public List<ServiceInstance> members(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    @GetMapping
    public String[] getMessages() {
        String[] messages = new String[]{"Message 1", "Message 2", "Message 3"};
        log.info("The messages are {}.", Arrays.toString(messages));
        return messages;
    }
}
