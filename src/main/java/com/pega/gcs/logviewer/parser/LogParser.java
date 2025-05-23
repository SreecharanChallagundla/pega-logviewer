/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.logviewer.LogViewer;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern.LogType;
import com.pega.gcs.logviewer.logfile.AlertLogPattern;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.logfile.Log4jPatternManager;
import com.pega.gcs.logviewer.logfile.LogPatternFactory;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public abstract class LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogParser.class);

    private AbstractLogPattern abstractLogPattern;

    private Charset charset;

    private Locale locale;

    private ZoneId modelZoneId;

    private ZoneId displayZoneId;

    private DateTimeFormatter modelDateTimeFormatter;

    private AtomicInteger logEntryIndex;

    protected abstract void parseV1(String line);

    protected abstract void parseV2(String line);

    protected abstract void parseV3(String line);

    public abstract void parseFinalInternal();

    public abstract LogEntryModel getLogEntryModel();

    private AtomicInteger processedCount;

    private boolean resetProcessedCount;

    private Pattern cloudkPattern;

    private Pattern cloudkDatePattern;

    private CloudKVersion cloudKVersion;

    private ObjectMapper objectMapper;

    protected enum CloudKVersion {
        // NULL - normal log entry
        // V0 - normal log entry but cloud/aws timestamp appended
        // DOCKER - pod json logs
        // V1 - cloudk Json entry
        // V2 - cloudk json entry
        // V3 - cloudk json entry
        NULL, V0, DOCKER, V1, V2, V3;
    }

    public LogParser(AbstractLogPattern abstractLogPattern, Charset charset, Locale locale, ZoneId displayZoneId) {

        this.abstractLogPattern = abstractLogPattern;
        this.charset = charset;
        this.locale = locale;
        this.displayZoneId = displayZoneId;

        this.modelZoneId = null;
        this.modelDateTimeFormatter = null;

        this.logEntryIndex = new AtomicInteger(0);

        this.processedCount = new AtomicInteger(0);
        this.resetProcessedCount = false;

        // String cloudkRegex = ".*?\\s(.*)";
        String cloudkRegex = "(?:\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z)?(.*)";

        cloudkPattern = Pattern.compile(cloudkRegex);

        // 2023-04-26T13:28:31.811Z
        String cloudkDateRegex = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z).*";
        cloudkDatePattern = Pattern.compile(cloudkDateRegex);

        cloudKVersion = null;

        this.objectMapper = new ObjectMapper();
    }

    public AbstractLogPattern getLogPattern() {
        return abstractLogPattern;
    }

    protected Charset getCharset() {
        return charset;
    }

    protected Locale getLocale() {
        return locale;
    }

    protected ZoneId getDisplayZoneId() {
        return displayZoneId;
    }

    protected void setDisplayZoneId(ZoneId displayZoneId) {
        this.displayZoneId = displayZoneId;
    }

    protected ZoneId getModelZoneId() {
        return modelZoneId;
    }

    protected void setModelZoneId(ZoneId modelZoneId) {
        this.modelZoneId = modelZoneId;
    }

    protected DateTimeFormatter getModelDateTimeFormatter() {
        return modelDateTimeFormatter;
    }

    protected void setModelDateTimeFormatter(DateTimeFormatter modelDateTimeFormatter) {
        this.modelDateTimeFormatter = modelDateTimeFormatter;
    }

    protected AtomicInteger getLogEntryIndex() {
        return logEntryIndex;
    }

    private ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void parse(String line) {

        CloudKVersion cloudKVersion = getOrSetCloudKVersion(line);

        switch (cloudKVersion) {

        case NULL:
        case V0:
        case V1:
            parseV1(line);
            break;
        case V2:
            parseV2(line);
            break;
        case V3:
            parseV3(line);
            break;
        default:
            parseV1(line);
            break;

        }
    }

    public void parseFinal() {

        parseFinalInternal();

        resetProcessedCount();

        // sort entries with timestamp.
        LogEntryModel logEntryModel = getLogEntryModel();
        List<LogEntryKey> logEntryKeyList = logEntryModel.getLogEntryKeyList();

        Collections.sort(logEntryKeyList);

        // moved from LogEntryModel.addLogEntry()
        HashMap<LogEntryKey, Integer> keyIndexMap = logEntryModel.getKeyIndexMap();

        if (keyIndexMap != null) {

            keyIndexMap.clear();

            for (int index = 0; index < logEntryKeyList.size(); index++) {

                LogEntryKey key = logEntryKeyList.get(index);

                keyIndexMap.put(key, index);
            }
        }
    }

    protected String quoteTimeStampChars(String input) {
        // put single quotes around text that isn't a supported dateformat char
        StringBuilder result = new StringBuilder();
        // ok to default to false because we also check for index zero below
        boolean lastCharIsDateFormat = false;

        for (int i = 0; i < input.length(); i++) {

            String thisVal = input.substring(i, i + 1);
            boolean thisCharIsDateFormat = DateTimeUtilities.VALID_DATEFORMAT_CHARS.contains(thisVal);
            // we have encountered a non-dateformat char
            if (!thisCharIsDateFormat && (i == 0 || lastCharIsDateFormat)) {
                result.append("'");
            }
            // we have encountered a dateformat char after previously
            // encountering a non-dateformat char
            if (thisCharIsDateFormat && i > 0 && !lastCharIsDateFormat) {
                result.append("'");
            }
            lastCharIsDateFormat = thisCharIsDateFormat;
            result.append(thisVal);
        }
        // append an end single-quote if we ended with non-dateformat char
        if (!lastCharIsDateFormat) {
            result.append("'");
        }
        return result.toString();
    }

    protected String getTimeStampFormat(String log4jPattern) {

        String timeStampFormat = null;
        String timeZoneStr = null;

        int index = log4jPattern.indexOf("%d");

        if (index != -1) {

            index = log4jPattern.indexOf("%d{");

            // use ISO8601 format
            if (index == -1) {
                timeStampFormat = DateTimeUtilities.DATEFORMAT_ISO8601;
            } else {
                // identify format
                int formatIndex = log4jPattern.substring(index).indexOf("}");

                timeStampFormat = log4jPattern.substring(index + "%d{".length(), index + formatIndex);

                if (timeStampFormat.equals("ABSOLUTE")) {
                    timeStampFormat = DateTimeUtilities.DATEFORMAT_ABSOLUTE;
                } else if ("".equals(timeStampFormat) || timeStampFormat.equals("ISO8601")) {
                    timeStampFormat = DateTimeUtilities.DATEFORMAT_ISO8601;
                } else if (timeStampFormat.equals("DATE")) {
                    timeStampFormat = DateTimeUtilities.DATEFORMAT_DATE;
                }

                // identify time zone-Optional
                // if the adjoining character is a curly brace
                int tzIndex = log4jPattern.substring(index + formatIndex + 1).indexOf("{");

                if (tzIndex == 0) {
                    tzIndex = index + formatIndex + 1;
                    timeZoneStr = log4jPattern.substring(tzIndex + "{".length(), log4jPattern.indexOf("}", tzIndex));
                    timeStampFormat = timeStampFormat + " z";
                }
            }

            // String quoteTimeStamp = quoteTimeStampChars(timeStampFormat);

            LOG.info("Using Timestamp format: " + timeStampFormat);

            DateTimeFormatter modelDateTimeFormatter = DateTimeFormatter.ofPattern(timeStampFormat);

            if (timeZoneStr != null) {

                ZoneId modelZoneId = ZoneId.of(timeZoneStr);

                setModelZoneId(modelZoneId);
            }

            setModelDateTimeFormatter(modelDateTimeFormatter);
        }

        return timeStampFormat;
    }

    protected String escapeRegexChars(String input) {
        String escapedStr = input;

        escapedStr = escapedStr.replaceAll("\\\\", "\\\\\\");
        escapedStr = escapedStr.replaceAll(Pattern.quote("*"), "\\\\*");
        escapedStr = escapedStr.replaceAll(Pattern.quote("]"), "\\\\]");
        escapedStr = escapedStr.replaceAll(Pattern.quote("["), "\\\\[");
        escapedStr = escapedStr.replaceAll(Pattern.quote("^"), "\\\\^");
        // escapedStr = escapedStr.replaceAll(Pattern.quote("$"), "\\\\$");
        escapedStr = escapedStr.replaceAll(Pattern.quote("."), "\\\\.");
        escapedStr = escapedStr.replaceAll(Pattern.quote("|"), "\\\\|");
        escapedStr = escapedStr.replaceAll(Pattern.quote("?"), "\\\\?");
        escapedStr = escapedStr.replaceAll(Pattern.quote("+"), "\\\\+");
        escapedStr = escapedStr.replaceAll(Pattern.quote("("), "\\\\(");
        escapedStr = escapedStr.replaceAll(Pattern.quote(")"), "\\\\)");
        escapedStr = escapedStr.replaceAll(Pattern.quote("-"), "\\\\-");
        escapedStr = escapedStr.replaceAll(Pattern.quote("{"), "\\\\{");
        escapedStr = escapedStr.replaceAll(Pattern.quote("}"), "\\\\}");
        escapedStr = escapedStr.replaceAll(Pattern.quote("#"), "\\\\#");
        escapedStr = escapedStr.replaceAll(Pattern.quote(" "), "\\\\s");

        return escapedStr;
    }

    public static LogParser getLogParser(String filename, List<String> readLineList, Charset charset, Locale locale,
            ZoneId displayZoneId) {

        LogParser logParser = null;

        Log4jPatternManager log4jPatternManager = Log4jPatternManager.getInstance();

        if (filename.toUpperCase().contains(LogViewer.GCP_GOC_FILE_SUFFIX_ALERT.toUpperCase())) {

            AlertLogPattern alertLogPattern = LogPatternFactory.getInstance().getAlertLogPattern();

            AlertLogParser alertLogParser = new GcpGocAlertLogParser(alertLogPattern, charset, locale, displayZoneId);

            int rowCount = tryLogParser(alertLogParser, readLineList);

            if (rowCount > 0) {
                // success
                LOG.info("creating GcpGocAlertLogParser for " + filename);
                logParser = alertLogParser;
            }

        } else if (filename.toUpperCase().contains("ALERT")) {
            // check if an Alert log file

            AlertLogPattern alertLogPattern = LogPatternFactory.getInstance().getAlertLogPattern();

            AlertLogParser alertLogParser = new AlertLogParser(alertLogPattern, charset, locale, displayZoneId);

            int rowCount = tryLogParser(alertLogParser, readLineList);

            if (rowCount > 0) {
                // success
                LOG.info("creating AlertLogParser for " + filename);
                logParser = alertLogParser;
            }

        } else if ((filename.toUpperCase().contains("CLUSTER")) || (filename.toUpperCase().contains("HAZELCAST"))
                || (filename.toUpperCase().contains("BIX"))) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultClusterLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayZoneId);

        } else if ((filename.toUpperCase().contains("DATAFLOW")) && (!filename.toUpperCase().contains("PEGARULES"))) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultDataflowLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayZoneId);

        } else if (filename.toUpperCase().contains("DDSMETRICS")) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultDdsMetricLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayZoneId);
        } else if (filename.toUpperCase().contains("ISTIO")) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultPegaCloudKIstioLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayZoneId);

        } else if ((filename.toUpperCase().contains("LOCALHOST_ACCESS_LOG"))) {

            Set<Log4jPattern> log4jPatternSet = log4jPatternManager.getDefaultPegaCloudKAccessLog4jPatternSet();

            logParser = getLog4jParser(readLineList, log4jPatternSet, charset, locale, displayZoneId);
        } else if ((filename.toUpperCase().contains(LogViewer.GCP_GOC_FILE_SUFFIX_RULES.toUpperCase()))) {

            JsonLogParser jsonLogParser = new JsonLogParser(charset, locale, displayZoneId);

            int rowCount = tryLogParser(jsonLogParser, readLineList);

            if (rowCount > 0) {
                // success
                LOG.info("creating JsonLogParser for " + filename);
                logParser = jsonLogParser;
            }
        }

        // check if a PegaRules log file
        if (logParser == null) {

            Set<Log4jPattern> pegaRulesLog4jPatternSet = log4jPatternManager.getDefaultRulesLog4jPatternSet();

            logParser = getLog4jParser(readLineList, pegaRulesLog4jPatternSet, charset, locale, displayZoneId);
        }

        return logParser;
    }

    public static LogParser getLogParserFromPattern(AbstractLogPattern abstractLogPattern, Charset charset,
            Locale locale, ZoneId displayZoneId) {

        LogParser logParser = null;

        LogType logType = abstractLogPattern.getLogType();

        switch (logType) {

        case PEGA_ALERT:
            logParser = new AlertLogParser((AlertLogPattern) abstractLogPattern, charset, locale, displayZoneId);
            break;
        case PEGA_RULES:
            logParser = new Log4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale, displayZoneId);
            break;
        case PEGA_CLUSTER:
            logParser = new ClusterLog4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayZoneId);
            break;
        case PEGA_DATAFLOW:
            logParser = new DataflowLog4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayZoneId);
            break;
        case PEGA_DDSMETRIC:
            logParser = new DdsMetricLog4jPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayZoneId);
            break;
        case PEGA_CLOUDK_ISTIO:
            logParser = new CloudKIstioLogPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayZoneId);
            break;
        case PEGA_CLOUDK_ACCESS_LOG:
            logParser = new CloudKAccessLogPatternParser((Log4jPattern) abstractLogPattern, charset, locale,
                    displayZoneId);
            break;
        default:
            break;
        }

        return logParser;
    }

    // currently assuming all pattern as of type pegarules. change when
    // implementing other types like WAS, WLS
    // with 8.6 changes, multiple patterns are now matching , hence selecting a pattern that returns the max nos of rows.
    public static LogParser getLog4jParser(List<String> readLineList, Set<Log4jPattern> log4jPatternSet,
            Charset charset, Locale locale, ZoneId displayZoneId) {

        LogParser logParser = null;

        for (Log4jPattern log4jPattern : log4jPatternSet) {

            LogParser currentLogParser = getLogParserFromPattern(log4jPattern, charset, locale, displayZoneId);

            int rowCount = tryLogParser(currentLogParser, readLineList);

            CloudKVersion cloudKVersion = currentLogParser.getCloudKVersion();

            // trying only once as we know the pattern for all other logs (cloudk v2, v3)
            if (!((CloudKVersion.NULL.equals(cloudKVersion) || CloudKVersion.V0.equals(cloudKVersion)
                    || CloudKVersion.V1.equals(cloudKVersion)))) {
                logParser = currentLogParser;

                break;
            }

            int logParserRowCount = (logParser != null) ? logParser.getProcessedCount() : 0;

            if (rowCount > logParserRowCount) {
                logParser = currentLogParser;
            }

        }

        if (logParser != null) {
            // success
            int logParserRowCount = logParser.getProcessedCount();

            LOG.info("Creating Log4jPatternParser: *rowCount: " + logParserRowCount + " Parser: " + logParser);
        }

        return logParser;
    }

    private static int tryLogParser(LogParser logParser, List<String> readLineList) {

        LOG.info("Trying LogParser " + logParser);

        for (String readLine : readLineList) {
            logParser.parse(readLine);
        }

        logParser.parseFinal();

        int rowCount = logParser.getProcessedCount();

        LOG.info("Trying LogParser " + logParser + " rowCount:" + rowCount);

        return rowCount;

    }

    protected int incrementAndGetProcessedCount() {

        if (resetProcessedCount) {
            processedCount.set(0);
            resetProcessedCount = false;
        }

        return processedCount.incrementAndGet();
    }

    protected void resetProcessedCount() {
        resetProcessedCount = true;
    }

    public int getProcessedCount() {
        return processedCount.get();
    }

    private String extractLogMessage(String line) {

        String logMessage = null;

        if (line != null) {

            Matcher cloudkPatternMatcher = cloudkPattern.matcher(line);

            if (cloudkPatternMatcher.find()) {
                logMessage = cloudkPatternMatcher.group(1);
            }
        }

        return logMessage;
    }

    protected CloudKVersion getCloudKVersion() {
        return cloudKVersion;
    }

    protected CloudKVersion getOrSetCloudKVersion(String line) {

        if (cloudKVersion == null) {

            String logMessage = extractLogMessage(line);

            if ((logMessage != null) && (!"".equals(logMessage.trim()))) {

                try {

                    ObjectMapper objectMapper = getObjectMapper();

                    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                    };

                    Map<String, Object> fieldMap = objectMapper.readValue(logMessage, typeRef);

                    Object timestampValue = fieldMap.get("@timestamp");
                    Object logValue = fieldMap.get("log");

                    if ((timestampValue == null) && (logValue == null)) {

                        cloudKVersion = CloudKVersion.V1;

                    } else {

                        if (logValue == null) {
                            cloudKVersion = CloudKVersion.V2;
                        } else {
                            cloudKVersion = CloudKVersion.V3;
                        }

                        LogEntryModel logEntryModel = getLogEntryModel();

                        AbstractLogPattern abstractLogPattern = getLogPattern();

                        if (abstractLogPattern != null) {

                            List<LogEntryColumn> cloudKLogEventColumnList = null;

                            LogType logType = abstractLogPattern.getLogType();

                            if (!LogType.PEGA_ALERT.equals(logType)) {

                                String cloudKTimestampPattern;

                                cloudKTimestampPattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSS]X";

                                ZoneId modelZoneId = ZoneOffset.UTC;
                                ZoneId displayZoneId = ZoneOffset.UTC;
                                DateTimeFormatter modelDateTimeFormatter = DateTimeFormatter
                                        .ofPattern(cloudKTimestampPattern);
                                setModelZoneId(modelZoneId);
                                setDisplayZoneId(displayZoneId);
                                setModelDateTimeFormatter(modelDateTimeFormatter);

                                logEntryModel.setModelZoneId(modelZoneId);
                                logEntryModel.setDisplayZoneId(displayZoneId);
                                logEntryModel.setModelDateTimeFormatter(modelDateTimeFormatter);
                            }

                            switch (logType) {

                            case PEGA_ALERT:
                                cloudKLogEventColumnList = null;
                                break;
                            case PEGA_CLUSTER:
                                cloudKLogEventColumnList = LogEntryColumn.getCloudKPegaClusterColumnList();
                                break;
                            case PEGA_DATAFLOW:

                                cloudKLogEventColumnList = new ArrayList<>();
                                cloudKLogEventColumnList.addAll(LogEntryColumn.getCloudKPegaDataflowColumnList());
                                cloudKLogEventColumnList.addAll(LogEntryColumn.getDataflowLogEventColumnList());

                                break;
                            case PEGA_DDSMETRIC:
                                cloudKLogEventColumnList = null;
                                break;
                            case PEGA_RULES:
                                cloudKLogEventColumnList = LogEntryColumn.getCloudKPegaRulesColumnList();
                                break;
                            default:
                                cloudKLogEventColumnList = null;
                                break;

                            }

                            if (cloudKLogEventColumnList != null) {
                                logEntryModel.updateLogEntryColumnList(cloudKLogEventColumnList);
                            }
                        }
                    }
                } catch (JacksonException e) {

                    // not a json, check for aws type logs.
                    Matcher cloudkDateMatcher = cloudkDatePattern.matcher(line);

                    if (cloudkDateMatcher.matches()) {
                        cloudKVersion = CloudKVersion.V0;
                    } else {
                        cloudKVersion = CloudKVersion.NULL;
                    }
                }

                LOG.info("Found Log type version as " + cloudKVersion);
            }
        }

        return cloudKVersion;
    }

    // removes the first timestamp string and parses the remaining string as JSON
    protected Map<String, Object> getCloudKFieldMap(String line) {

        Map<String, Object> fieldMap = null;

        try {

            String json = extractLogMessage(line);

            ObjectMapper objectMapper = getObjectMapper();

            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };

            fieldMap = objectMapper.readValue(json, typeRef);

        } catch (JacksonException e) {
            LOG.error("Error parsing log json data", e.getMessage());
        }

        return fieldMap;
    }

    protected Map<String, String> getJsonFieldMap(String line) {

        Map<String, String> fieldMap = null;

        try {

            ObjectMapper objectMapper = getObjectMapper();

            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };

            fieldMap = objectMapper.readValue(line, typeRef);

        } catch (JacksonException e) {
            LOG.error("Error parsing log json data", e.getMessage());
        }

        return fieldMap;
    }

    // only to be called from V1 parsing
    protected String getLineFromCloudK(String line) {

        String lineFromCloudK = null;

        CloudKVersion cloudKVersion = getOrSetCloudKVersion(line);

        switch (cloudKVersion) {

        case NULL:
            lineFromCloudK = line;
            break;

        case V0:
            lineFromCloudK = extractLogMessage(line);
            break;

        case V1:
            try {

                Map<String, Object> fieldMap = getCloudKFieldMap(line);

                if (fieldMap != null) {
                    lineFromCloudK = (String) fieldMap.get("message");
                } else {
                    lineFromCloudK = line;
                }
            } catch (Exception e) {
                LOG.error("Error parsing CloudK Json", e.getMessage());

                lineFromCloudK = line;
            }

            break;

        case V2:
        case V3:
            lineFromCloudK = line;
            break;

        default:
            lineFromCloudK = line;
            break;

        }

        return lineFromCloudK;
    }

    protected <T, V> String getJsonString(Map<T, V> fieldMap) {

        String jsonString = null;

        try {
            ObjectMapper objectMapper = getObjectMapper();
            jsonString = objectMapper.writeValueAsString(fieldMap);
        } catch (JsonProcessingException e) {
            LOG.error("Error converting map to Json string", e);
        }

        return jsonString;
    }
}
