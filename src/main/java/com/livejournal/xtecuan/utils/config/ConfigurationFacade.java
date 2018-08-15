/*
 * Copyright 2016 xtecuan.
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
package com.livejournal.xtecuan.utils.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 *
 * @author xtecuan
 */

public class ConfigurationFacade {

    public static final Map<String, String> env = System.getenv();
    public static final String ENV_VARIABLE_TO_CONFIG_FILE = "XCONFIG_FILE";
    public static final String TOKEN_KEY = "api.token";
    private static final String API_NAME = "api.name";
    private static final String API_VERSION = "api.version";
    public static final String APP_CONFIG = "application.properties";
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ConfigurationFacade.class);
    public static final String XCONFIG_FILE = getEnvironmentVariable(ENV_VARIABLE_TO_CONFIG_FILE);
    public static PropertiesConfiguration config = (XCONFIG_FILE != null && !XCONFIG_FILE.equals("")) ? getConfiguration() : getConfiguration(APP_CONFIG);

    private static String getEnvironmentVariable(String name) {
        return env.getOrDefault(name, "");
    }

    private static PropertiesConfiguration getConfiguration() {
        String configFile = getEnvironmentVariable(ENV_VARIABLE_TO_CONFIG_FILE);

        PropertiesConfiguration out = new PropertiesConfiguration();
        try {
            if (configFile != null && configFile.length() > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(new File(configFile)),
                        "UTF-8"));
                out.read(br);
                System.out.println("Configuration loaded from "+configFile+"!");
            }

        } catch (ConfigurationException ex) {
            logger.error("Error creating config: ", ex);
        } catch (IOException ex) {
            logger.error("Error reading the config: ", ex);
        }
        return out;
    }

    private static PropertiesConfiguration getConfiguration(String configFileName) {
        PropertiesConfiguration out = new PropertiesConfiguration();
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName),
                    "UTF-8"));
            out.read(br);
            System.out.println("Configuration (Default) loaded from "+configFileName+"!");
        } catch (ConfigurationException ex) {
            logger.error("Error creating config: ", ex);
        } catch (IOException ex) {
            logger.error("Error reading the config: ", ex);
        }
        return out;
    }

    public String getValue(String key) {
        return config.getString(key);
    }

    public Integer getInteger(String key) {
        return config.getInt(key);
    }

    public String getApiToken() {
        return getValue(TOKEN_KEY);
    }

    public String getApiName() {
        return getValue(API_NAME);
    }

    public String getApiVersion() {
        return getValue(API_VERSION);
    }

    public Map<String, String> getApiMetadata() {
        Map<String, String> result = new HashMap<>();
        result.put("apiName", getApiName());
        result.put("apiVersion", getApiVersion());
        return result;
    }

}
