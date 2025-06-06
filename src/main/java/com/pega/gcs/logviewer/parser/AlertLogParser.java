/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.logfile.AlertLogPattern;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.PALStatisticName;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertLogParser extends LogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogParser.class);

    private int alertVersion;

    private StringBuilder fullLogEntryTextSB;

    private int capturedColumnCount;

    // store the index of 'messageid' column so as to identify colour
    private int messageIDIndex;

    private int timestampIndex;

    private int observedKPIIndex;

    private int palDataIndex;

    private AlertLogEntryModel alertLogEntryModel;

    private Pattern alertDatePattern;

    public AlertLogParser(AlertLogPattern alertLogPattern, Charset charset, Locale locale, ZoneId displayZoneId) {

        super(alertLogPattern, charset, locale, displayZoneId);

        alertVersion = -1;

        fullLogEntryTextSB = new StringBuilder();
        capturedColumnCount = 0;

        // this sets the parsers model zone id and model datetimeformatter
        getTimeStampFormat("%d{}{GMT}");

        String threadRegEx = "(\\d{4}-\\d{2}-\\d{2}[ ]\\d{2}:\\d{2}:\\d{2},\\d{3}[ ]GMT)";
        alertDatePattern = Pattern.compile(threadRegEx);

        DateTimeFormatter modelDateTimeFormatter = getModelDateTimeFormatter();
        ZoneId modelZoneId = getModelZoneId();

        alertLogEntryModel = new AlertLogEntryModel(modelDateTimeFormatter, modelZoneId, displayZoneId);
    }

    @Override
    public String toString() {
        return "AlertLogParser [" + alertVersion + "]";
    }

    @Override
    protected void parseV1(String line) {

        line = getLineFromCloudK(line);

        setupLogEntryColumnList(line);

        AlertLogEntryModel alertLogEntryModel = getLogEntryModel();
        List<LogEntryColumn> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

        // LEM column list is never null
        int logEntryColumnListSize = logEntryColumnList.size();

        if (logEntryColumnListSize > 0) {

            String[] fields = null;

            StringBuilder fullLogEntryTextSB = getFullLogEntryTextSB();

            // logEntryColumnListSize has additional Line column
            if ((fullLogEntryTextSB.length() > 0) && (capturedColumnCount < (logEntryColumnListSize - 1))) {

                fullLogEntryTextSB.append(line);
                fields = fullLogEntryTextSB.toString().split("\\*");
                capturedColumnCount = fields.length;

            } else {

                buildLogEntry(null);

                fullLogEntryTextSB.append(line);
                fields = fullLogEntryTextSB.toString().split("\\*");
                capturedColumnCount = fields.length;
            }
        } else {
            LOG.info("discarding empty line in the begining");
        }
    }

    @Override
    protected void parseV2(String line) {

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            String message = (String) fieldMap.get("message");

            processClouldKMessage(message);
        }
    }

    @Override
    protected void parseV3(String line) {

        Map<String, Object> fieldMap = getCloudKFieldMap(line);

        if (fieldMap != null) {

            @SuppressWarnings("unchecked")
            Map<String, String> logMap = (Map<String, String>) fieldMap.get("log");

            String message = (String) logMap.get("message");

            processClouldKMessage(message);
        }
    }

    protected void processClouldKMessage(String message) {

        setupLogEntryColumnList(message);

        AlertLogEntryModel alertLogEntryModel = getLogEntryModel();
        List<LogEntryColumn> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

        // LEM column list is never null
        int logEntryColumnListSize = logEntryColumnList.size();

        if (logEntryColumnListSize > 0) {

            StringBuilder fullLogEntryTextSB = getFullLogEntryTextSB();

            fullLogEntryTextSB.append(message);

            buildLogEntry(null);
        }
    }

    private void setupLogEntryColumnList(String line) {

        AlertLogEntryModel alertLogEntryModel = getLogEntryModel();
        List<LogEntryColumn> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

        if ((line != null) && (!line.isEmpty()) && ((logEntryColumnList == null) || (logEntryColumnList.isEmpty()))) {

            int oldStyleIndex = 0;

            String[] fields = line.split("\\*");

            int fieldsLen = fields.length;

            // 22 is the lowest count of fields, for Alert Version 4.
            if (fieldsLen >= 22) {

                int alertVersion = Integer.parseInt(fields[1]);

                setAlertVersion(alertVersion);

                // // 37 is max column count for v8 alert
                // if (fieldsLen > 37) {
                // oldStyleIndex = 12;
                // }

                switch (alertVersion) {

                case 4:
                    logEntryColumnList = getAlertColumnListV4();
                    setTimestampIndex(oldStyleIndex + 0);
                    setMessageIDIndex(oldStyleIndex + 2);
                    setObservedKPIIndex(oldStyleIndex + 3);
                    setPalDataIndex(oldStyleIndex + 20);
                    break;

                case 5:
                    logEntryColumnList = getAlertColumnListV5();
                    setTimestampIndex(oldStyleIndex + 0);
                    setMessageIDIndex(oldStyleIndex + 2);
                    setObservedKPIIndex(oldStyleIndex + 3);
                    setPalDataIndex(oldStyleIndex + 20);
                    break;

                case 6:
                    logEntryColumnList = getAlertColumnListV6();
                    setTimestampIndex(oldStyleIndex + 0);
                    setMessageIDIndex(oldStyleIndex + 2);
                    setObservedKPIIndex(oldStyleIndex + 3);
                    setPalDataIndex(oldStyleIndex + 22);
                    break;

                case 7:
                    logEntryColumnList = getAlertColumnListV7();
                    setTimestampIndex(oldStyleIndex + 0);
                    setMessageIDIndex(oldStyleIndex + 2);
                    setObservedKPIIndex(oldStyleIndex + 3);
                    setPalDataIndex(oldStyleIndex + 24);
                    break;

                case 8:
                    logEntryColumnList = getAlertColumnListV8();
                    setTimestampIndex(oldStyleIndex + 0);
                    setMessageIDIndex(oldStyleIndex + 2);
                    setObservedKPIIndex(oldStyleIndex + 3);
                    setPalDataIndex(oldStyleIndex + 29);
                    break;

                default:
                    // set to v6 alert.
                    logEntryColumnList = getAlertColumnListV6();
                    setTimestampIndex(oldStyleIndex + 0);
                    setMessageIDIndex(oldStyleIndex + 2);
                    setObservedKPIIndex(oldStyleIndex + 3);
                    setPalDataIndex(oldStyleIndex + 22);
                    break;
                }

                LOG.info("logEntryColumnList: " + logEntryColumnList);

                alertLogEntryModel.updateLogEntryColumnList(logEntryColumnList);
            } else {
                LOG.info("discarding line: " + line);
            }
        }

    }

    protected StringBuilder getFullLogEntryTextSB() {
        return fullLogEntryTextSB;
    }

    protected void setAlertVersion(int alertVersion) {
        this.alertVersion = alertVersion;
    }

    protected void setMessageIDIndex(int messageIDIndex) {
        this.messageIDIndex = messageIDIndex;
    }

    protected void setTimestampIndex(int timestampIndex) {
        this.timestampIndex = timestampIndex;
    }

    protected void setObservedKPIIndex(int observedKPIIndex) {
        this.observedKPIIndex = observedKPIIndex;
    }

    protected void setPalDataIndex(int palDataIndex) {
        this.palDataIndex = palDataIndex;
    }

    @Override
    public void parseFinalInternal() {

        buildLogEntry(null);

        alertLogEntryModel.processAlertMessageReportModels();
    }

    @Override
    public AlertLogEntryModel getLogEntryModel() {
        return alertLogEntryModel;
    }

    protected AlertLogEntry buildLogEntry(List<String> additionalColumnValueList) {

        AlertLogEntry alertLogEntry = null;

        StringBuilder fullLogEntryTextSB = getFullLogEntryTextSB();

        if (fullLogEntryTextSB.length() > 0) {

            AlertLogEntryModel alertLogEntryModel = getLogEntryModel();
            AtomicInteger logEntryIndex = getLogEntryIndex();

            List<LogEntryColumn> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

            try {

                logEntryIndex.incrementAndGet();

                int logEntryColumnListSize = logEntryColumnList.size();

                // adding trim() as some alert logs are copied from IU and has a space after the last '*'.
                String logEntryText = fullLogEntryTextSB.toString().trim();
                String[] fields = logEntryText.split("\\*");

                // logEntryColumnListSize has additional LINE column
                if (fields.length != (logEntryColumnListSize - 1)) {
                    LOG.info("Problem - found additional * in the alert entry: " + logEntryIndex);

                    // try fixing the alert
                    logEntryText = logEntryText.replaceAll("\\*/", "ASTERIX/");

                    fields = logEntryText.split("\\*");
                }

                DateTimeFormatter modelDateTimeFormatter = alertLogEntryModel.getModelDateTimeFormatter();
                ZoneId modelZoneId = alertLogEntryModel.getModelZoneId();

                String timestampStr = fields[timestampIndex];
                long logEntryTime = -1;

                try {

                    logEntryTime = LogViewerUtil.getTimeMillis(timestampStr, modelDateTimeFormatter, modelZoneId);

                } catch (Exception pe) {

                    LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + logEntryText + "]", pe);

                    // possibly a older style alert. extract the date string and
                    // parse it again
                    Matcher alertDateMatcher = alertDatePattern.matcher(timestampStr);

                    if (alertDateMatcher.find()) {
                        int count = alertDateMatcher.groupCount();
                        timestampStr = alertDateMatcher.group(count);

                        try {

                            logEntryTime = LogViewerUtil.getTimeMillis(timestampStr, modelDateTimeFormatter,
                                    modelZoneId);

                        } catch (Exception pe2) {
                            LOG.error("not able to parse [" + timestampStr + "]", pe2);
                        }
                    }
                }

                ArrayList<String> logEntryColumnValueList;

                logEntryColumnValueList = new ArrayList<String>();
                logEntryColumnValueList.add(String.valueOf(logEntryIndex));
                logEntryColumnValueList.addAll(Arrays.asList(fields));

                if (additionalColumnValueList != null) {
                    logEntryColumnValueList.addAll(additionalColumnValueList);
                }

                String messageIdStr = fields[messageIDIndex];

                AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

                Integer alertId = alertMessageListProvider.getAlertId(messageIdStr);

                String observedKPIStr = fields[observedKPIIndex];
                long observedKPI = Long.parseLong(observedKPIStr);

                boolean criticalAlertEntry = false;

                List<String> criticalAlertList = alertMessageListProvider.getCriticalAlertList();

                if (criticalAlertList.contains(messageIdStr)) {
                    criticalAlertEntry = true;
                }

                // parse PAL data
                String palDataStr = fields[palDataIndex];
                Number[] palDataValueArray = parsePALData(palDataStr);

                LogEntryKey logEntryKey = new LogEntryKey(logEntryIndex.intValue(), logEntryTime);

                alertLogEntry = new AlertLogEntry(logEntryKey, logEntryColumnValueList, logEntryText, alertId,
                        observedKPI, criticalAlertEntry, palDataValueArray);

                alertLogEntryModel.addLogEntry(alertLogEntry, logEntryColumnValueList, getCharset(), getLocale());

                // update the processed counter
                incrementAndGetProcessedCount();

                fullLogEntryTextSB.setLength(0); // Clear contents
            } catch (Exception e) {
                LOG.error("Error parsing Log text: \n" + fullLogEntryTextSB.toString());
                LOG.error("Error parsing Log index: " + logEntryIndex, e);

                // discard the previous accumulated text
                fullLogEntryTextSB.setLength(0);
            }
        }

        return alertLogEntry;
    }

    private Number[] parsePALData(String palDataStr) {

        Number[] palDataValueArray = new Number[PALStatisticName.values().length];

        NumberFormat nf = NumberFormat.getInstance(getLocale());

        if ((palDataStr != null) && (!"".equals(palDataStr)) && (!"NA".equals(palDataStr))) {

            String[] palStatArray = palDataStr.split(";", 0);

            for (String palStat : palStatArray) {

                String[] palStatNameValue = palStat.split("=", 2);

                String palStatName = palStatNameValue[0];
                String palStatValue = null;

                PALStatisticName palStatisticName = null;

                try {
                    palStatisticName = PALStatisticName.valueOf(palStatName);

                    if (palStatNameValue.length == 2) {

                        alertLogEntryModel.addPALStatisticColumn(palStatisticName);

                        palStatValue = palStatNameValue[1];

                        try {
                            Number parseValue = nf.parse(palStatValue);

                            int index = palStatisticName.ordinal();

                            palDataValueArray[index] = parseValue;
                        } catch (ParseException pe) {
                            LOG.error("Unable to parse PAL statistic: " + palStatisticName + " palStatValue: "
                                    + palStatValue, pe);
                        }
                    }

                } catch (Exception e) {
                    // such stat name doesn't exist, ignore for now.
                    LOG.error("palStatisticName: " + palStatName + " not found in the list", e);
                }
            }
        }

        return palDataValueArray;
    }

    private static List<LogEntryColumn> getAlertColumnListV4() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV5() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV6() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.PEGATHREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    private static List<LogEntryColumn> getAlertColumnListV7() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.TENANTID);
        alertColumnList.add(LogEntryColumn.TENANTIDHASH);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.PEGATHREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

    protected static List<LogEntryColumn> getAlertColumnListV8() {

        List<LogEntryColumn> alertColumnList = new ArrayList<>();

        alertColumnList.add(LogEntryColumn.LINE);
        alertColumnList.add(LogEntryColumn.TIMESTAMP);
        alertColumnList.add(LogEntryColumn.VERSION);
        alertColumnList.add(LogEntryColumn.MESSAGEID);
        alertColumnList.add(LogEntryColumn.OBSERVEDKPI);
        alertColumnList.add(LogEntryColumn.THRESHOLDKPI);
        alertColumnList.add(LogEntryColumn.NODEID);
        alertColumnList.add(LogEntryColumn.TENANTID);
        alertColumnList.add(LogEntryColumn.TENANTIDHASH);
        alertColumnList.add(LogEntryColumn.REQUESTORID);
        alertColumnList.add(LogEntryColumn.USERID);
        alertColumnList.add(LogEntryColumn.WORKPOOL);
        alertColumnList.add(LogEntryColumn.RULEAPPNAMEANDVERSION);
        alertColumnList.add(LogEntryColumn.ENCODEDRULESET);
        alertColumnList.add(LogEntryColumn.PERSONALRULESETYN);
        alertColumnList.add(LogEntryColumn.INTERACTIONNUMBER);
        alertColumnList.add(LogEntryColumn.CORRELATIONID);
        alertColumnList.add(LogEntryColumn.ALERTNUMBER);
        alertColumnList.add(LogEntryColumn.THREAD);
        alertColumnList.add(LogEntryColumn.PEGATHREAD);
        alertColumnList.add(LogEntryColumn.LOGGER);
        alertColumnList.add(LogEntryColumn.STACK);
        alertColumnList.add(LogEntryColumn.LASTINPUT);
        alertColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        alertColumnList.add(LogEntryColumn.LASTSTEP);
        alertColumnList.add(LogEntryColumn.CLIENTPAGELOADID);
        alertColumnList.add(LogEntryColumn.ISSTATELESSAPP);
        alertColumnList.add(LogEntryColumn.CLIENTREQUESTID);
        alertColumnList.add(LogEntryColumn.FUTURE3);
        alertColumnList.add(LogEntryColumn.TRACELIST);
        alertColumnList.add(LogEntryColumn.PALDATA);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGECLASS);
        alertColumnList.add(LogEntryColumn.PRIMARYPAGENAME);
        alertColumnList.add(LogEntryColumn.STEPPAGECLASS);
        alertColumnList.add(LogEntryColumn.STEPPAGENAME);
        alertColumnList.add(LogEntryColumn.PRSTACKTRACE);
        alertColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);
        alertColumnList.add(LogEntryColumn.MESSAGE);

        return alertColumnList;
    }

}
