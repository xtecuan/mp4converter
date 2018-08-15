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
package com.livejournal.xtecuan.utils.mp4converter;

import com.livejournal.xtecuan.utils.config.ConfigurationFacade;
import com.livejournal.xtecuan.utils.templater.TemplateLoader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

/**
 *
 * @author xtecuan
 */
public class MP4Converter {

    private static final long TIMEOUT = 120000;
    private static final ConfigurationFacade config = new ConfigurationFacade();
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MP4Converter.class);
    private static final TemplateLoader templater = new TemplateLoader();

    public static File getAudioFile(File video, File audioFolder) {
        int index = video.getName().lastIndexOf(config.getValue("videos.from.ext"));
        String newName = video.getName().substring(0, index) + config.getValue("videos.to.ext");
        return new File(audioFolder.getPath(), newName);
    }

    public static Map<String, Object> getCommandData(File video, File audio) {
        Map<String, Object> data = new HashMap<>();
        data.put("from", video.getPath());
        data.put("to", audio.getPath());
        return data;
    }

    public static String getCommand(File video, File audio) {
        return templater.getFilledHtmlTemplate(getCommandData(video, audio), config.getValue("conversion.command"));
    }

    public static void executeConversion(File video, File audio) throws IOException {
        String command = getCommand(video, audio);
        logger.info("Executing: " + command);
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(TIMEOUT);
        executor.setWatchdog(watchdog);
        int exitValue = executor.execute(cmdLine);

    }

    public static void main(String[] args) {

        logger.info(config.getValue("videos.scan.path"));
        File folder = new File(config.getValue("videos.scan.path"));
        File audioFolder = new File(folder.getPath(), config.getValue("output.path"));
        if (!audioFolder.exists()) {
            if (audioFolder.mkdirs()) {
                logger.info(audioFolder.getPath() + " created !!!");
            }
        } else {
            logger.info(audioFolder.getPath() + " already exists !!!");
        }

        File[] videos = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(config.getValue("videos.from.ext"));
            }
        });

        for (int i = 0; i < videos.length; i++) {
            File video = videos[i];
            logger.info("Processing video: " + video.getPath());
            File audio = getAudioFile(video, audioFolder);
            logger.info("Output File: " + audio.getPath());
            try {
                executeConversion(video, audio);
            }
            catch (Exception e) {
                logger.error("Error executing the command: ", e);
            }
        }
    }
}
