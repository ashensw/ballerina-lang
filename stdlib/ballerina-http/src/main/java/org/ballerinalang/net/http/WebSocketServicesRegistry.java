/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.net.http;

import org.ballerinalang.connector.api.Annotation;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.net.uri.URITemplate;
import org.ballerinalang.net.uri.URITemplateException;
import org.ballerinalang.net.uri.parser.Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.websocket.WebSocketMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Store all the WebSocket serviceEndpointsTemplate here.
 */
public class WebSocketServicesRegistry {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServicesRegistry.class);
    private CopyOnWriteArrayList<String> sortedServiceURIs = new CopyOnWriteArrayList<>();
    private URITemplate<WebSocketService, WebSocketMessage> uriTemplate;

    public WebSocketServicesRegistry() {
    }

    public void registerService(WebSocketService service) {
        String basePath = findFullWebSocketUpgradePath(service);
        if (basePath == null) {
            basePath = "/";
        }
        basePath = urlDecode(basePath);
        service.setBasePath(basePath);
        // TODO: Add websocket services to the service registry when service creation get available.
        try {
            getUriTemplate().parse(basePath, service, new WebSocketDataElementFactory());
        } catch (URITemplateException | UnsupportedEncodingException e) {
            throw new BallerinaConnectorException(e.getMessage());
        }
        logger.info("Service deployed : " + service.getName() + " with context " + basePath);

    }

    public URITemplate<WebSocketService, WebSocketMessage> getUriTemplate() throws URITemplateException {
        if (uriTemplate == null) {
            uriTemplate = new URITemplate<>(new Literal<>(new WebSocketDataElement(), "/"));
        }
        return uriTemplate;
    }

    private String urlDecode(String basePath) {
        try {
            basePath = URLDecoder.decode(basePath, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new BallerinaConnectorException(e.getMessage());
        }
        return basePath;
    }

    /**
     * Find the Full path for WebSocket upgrade.
     *
     * @param service {@link WebSocketService} which the full path should be found.
     * @return the full path of the WebSocket upgrade.
     */
    private String findFullWebSocketUpgradePath(WebSocketService service) {
        // Find Base path for WebSocket
        Annotation configAnnotation = WebSocketUtil.getServiceConfigAnnotation(service,
                                                                               HttpConstants.PROTOCOL_PACKAGE_HTTP);
        String basePath = null;
        if (configAnnotation != null) {
            Struct annStruct = configAnnotation.getValue();
            String annotationValue = annStruct.getStringField(HttpConstants.ANN_CONFIG_ATTR_BASE_PATH);
            if (annotationValue != null && !annotationValue.trim().isEmpty()) {
                basePath = refactorUri(annotationValue);
            }
        }
        return basePath;
    }

    /**
     * Refactor the given URI.
     *
     * @param uri URI to refactor.
     * @return refactored URI.
     */
    public String refactorUri(String uri) {
        if (uri.startsWith("\"")) {
            uri = uri.substring(1, uri.length() - 1);
        }

        if (!uri.startsWith("/")) {
            uri = "/".concat(uri);
        }

        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }


    public void addUpgradableServiceByName(WebSocketService service, String basePath) {
        basePath = urlDecode(basePath);
        service.setBasePath(basePath);
        try {
            getUriTemplate().parse(basePath, service, new WebSocketDataElementFactory());
        } catch (URITemplateException | UnsupportedEncodingException e) {
            throw new BallerinaConnectorException(e.getMessage());
        }
        logger.info("Service deployed : " + service.getName() + " with context " + basePath);
    }

}
