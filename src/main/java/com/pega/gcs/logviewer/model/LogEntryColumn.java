/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class LogEntryColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    //                                                                                                        displayName         prefColumnWidth    horizontalAlignment visibleColumn filterable
    public static final LogEntryColumn ALERTNUMBER           = new LogEntryColumn("ALERTNUMBER"             , "Alert Number"             , 100 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn APP                   = new LogEntryColumn("APP"                     , "App"                      , 70 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn INTERACTIONNUMBER     = new LogEntryColumn("INTERACTIONNUMBER"       , "Interaction Number"       , 100 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn LASTINPUT             = new LogEntryColumn("LASTINPUT"               , "Last Input"               , 200 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn LASTSTEP              = new LogEntryColumn("LASTSTEP"                , "Last Step"                , 200 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn LEVEL                 = new LogEntryColumn("LEVEL"                   , "Level"                    , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn LINE                  = new LogEntryColumn("LINE"                    , "Line"                     , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn LOGGER                = new LogEntryColumn("LOGGER"                  , "Logger"                   , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn MESSAGE               = new LogEntryColumn("MESSAGE"                 , "Message"                  , 900 , SwingConstants.LEFT   , true  , false );
    public static final LogEntryColumn MESSAGEID             = new LogEntryColumn("MESSAGEID"               , "Message Id"               , 100 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn NODEID                = new LogEntryColumn("NODEID"                  , "Node Id"                  , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn OBSERVEDKPI           = new LogEntryColumn("OBSERVEDKPI"             , "Observed KPI"             , 90  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn PEGATHREAD            = new LogEntryColumn("PEGATHREAD"              , "Pega Thread"              , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn REQUESTORID           = new LogEntryColumn("REQUESTORID"             , "Requestor Id"             , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn STACK                 = new LogEntryColumn("STACK"                   , "Stack"                    , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn TENANTID              = new LogEntryColumn("TENANTID"                , "Tenant Id"                , 50  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn THREAD                = new LogEntryColumn("THREAD"                  , "Thread"                   , 110 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn THRESHOLDKPI          = new LogEntryColumn("THRESHOLDKPI"            , "Threshold KPI"            , 110 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn TIMESTAMP             = new LogEntryColumn("TIMESTAMP"               , "Timestamp"                , 160 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn USERID                = new LogEntryColumn("USERID"                  , "User Id"                  , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn ALERTTYPE             = new LogEntryColumn("ALERTTYPE"               , "Alert Type"               , 77  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITCORRELATION1     = new LogEntryColumn("AUDITCORRELATION1"       , "Audit Correlation 1"      , 147 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITCORRELATION2     = new LogEntryColumn("AUDITCORRELATION2"       , "Audit Correlation 2"      , 147 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITEVENT            = new LogEntryColumn("AUDITEVENT"              , "Audit Event"              , 85  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITKEY              = new LogEntryColumn("AUDITKEY"                , "Audit Key"                , 70  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITNAME             = new LogEntryColumn("AUDITNAME"               , "Audit Name"               , 77  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITRULESET          = new LogEntryColumn("AUDITRULESET"            , "Audit Ruleset"            , 100 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITRULESETVERSION   = new LogEntryColumn("AUDITRULESETVERSION"     , "Audit Ruleset Version"    , 162 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn AUDITTYPE             = new LogEntryColumn("AUDITTYPE"               , "Audit Type"               , 77  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn CLASS                 = new LogEntryColumn("CLASS"                   , "Class"                    , 39  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn ENCODEDRULESET        = new LogEntryColumn("ENCODEDRULESET"          , "Encoded Ruleset"          , 116 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn FILE                  = new LogEntryColumn("FILE"                    , "File"                     , 31  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn FIRSTACTIVITY         = new LogEntryColumn("FIRSTACTIVITY"           , "First Activity"           , 200 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn INSTANCE_ID           = new LogEntryColumn("INSTANCE_ID"             , "Instance Id"              , 85  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn LOCATIONINFO          = new LogEntryColumn("LOCATIONINFO"            , "Location Info"            , 100 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn LOG4JID               = new LogEntryColumn("LOG4JID"                 , "Log4j Id"                 , 62  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn METHOD                = new LogEntryColumn("METHOD"                  , "Method"                   , 47  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn NDC                   = new LogEntryColumn("NDC"                     , "NDC"                      , 24  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn PALDATA               = new LogEntryColumn("PALDATA"                 , "PAL Data"                 , 62  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn PARAMETERPAGEDATA     = new LogEntryColumn("PARAMETERPAGEDATA"       , "Parameter Page Data"      , 147 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn PERSONALRULESETYN     = new LogEntryColumn("PERSONALRULESETYN"       , "Personal Ruleset YN"      , 147 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn PRIMARYPAGECLASS      = new LogEntryColumn("PRIMARYPAGECLASS"        , "Primary Page Class"       , 139 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn PRIMARYPAGENAME       = new LogEntryColumn("PRIMARYPAGENAME"         , "Primary Page Name"        , 131 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn PRSTACKTRACE          = new LogEntryColumn("PRSTACKTRACE"            , "PR Stacktrace"            , 100 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn RELATIVETIME          = new LogEntryColumn("RELATIVETIME"            , "Relative Time"            , 100 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn RULEAPPNAMEANDVERSION = new LogEntryColumn("RULEAPPNAMEANDVERSION"   , "Rule Appname And Version" , 185 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn STEPPAGECLASS         = new LogEntryColumn("STEPPAGECLASS"           , "Step Page Class"          , 116 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn STEPPAGENAME          = new LogEntryColumn("STEPPAGENAME"            , "Step Page Name"           , 108 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn TENANTIDHASH          = new LogEntryColumn("TENANTIDHASH"            , "Tenant Id Hash"           , 108 , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn TRACELIST             = new LogEntryColumn("TRACELIST"               , "Trace List"               , 77  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn VERSION               = new LogEntryColumn("VERSION"                 , "Version"                  , 54  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn WORKPOOL              = new LogEntryColumn("WORKPOOL"                , "Workpool"                 , 62  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn CORRELATIONID         = new LogEntryColumn("CORRELATIONID"           , "CorrelationID"            , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn CLIENTPAGELOADID      = new LogEntryColumn("CLIENTPAGELOADID"        , "ClientPageLoadId"         , 54  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn ISSTATELESSAPP        = new LogEntryColumn("ISSTATELESSAPP"          , "Stateless Application"    , 54  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn CLIENTREQUESTID       = new LogEntryColumn("CLIENTREQUESTID"         , "Client Request ID"        , 220 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn FUTURE3               = new LogEntryColumn("FUTURE3"                 , "Future3"                  , 54  , SwingConstants.CENTER , false , false );
    public static final LogEntryColumn DELTA                 = new LogEntryColumn("DELTA"                   , "Delta"                    , 70  , SwingConstants.RIGHT  , true  , false );

    public static final LogEntryColumn EVENT_TYPE            = new LogEntryColumn("EVENT TYPE"              , "EVENT TYPE"               , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn SENDER_NODE_ID        = new LogEntryColumn("SENDER NODE ID"          , "SENDER NODE ID"           , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn RUN_ID                = new LogEntryColumn("RUN ID"                  , "RUN ID"                   , 160 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn ORIGINATOR            = new LogEntryColumn("ORIGINATOR"              , "ORIGINATOR"               , 160 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn REASON                = new LogEntryColumn("REASON"                  , "REASON"                   , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn PARTITION_STATUS      = new LogEntryColumn("PARTITION STATUS"        , "PARTITION STATUS"         , 160 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn PREVIOUS_STATUS       = new LogEntryColumn("PREVIOUS STATUS"         , "PREVIOUS STATUS"          , 160 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn INTENTION             = new LogEntryColumn("INTENTION"               , "INTENTION"                , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn PARTITIONS            = new LogEntryColumn("PARTITIONS"              , "PARTITIONS"               , 200 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn THREAD_NAME           = new LogEntryColumn("THREAD NAME"             , "THREAD NAME"              , 300 , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn EVENT                 = new LogEntryColumn("EVENT"                   , "EVENT"                    , 160 , SwingConstants.CENTER , true  , true  );

    public static final LogEntryColumn START_TIME            = new LogEntryColumn("START_TIME"              ,"START_TIME"                , 160 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQ_PATH              = new LogEntryColumn("REQ_PATH"                ,"REQ_PATH"                  , 500 , SwingConstants.LEFT   , true  , true  );
    public static final LogEntryColumn RESPONSE_CODE         = new LogEntryColumn("RESPONSE_CODE"           ,"RESPONSE_CODE"             , 70  , SwingConstants.CENTER , true  , true  );
    public static final LogEntryColumn RESPONSE_FLAGS        = new LogEntryColumn("RESPONSE_FLAGS"          ,"RESPONSE_FLAGS"            , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn RESPONSE_CODE_DETAILS = new LogEntryColumn("RESPONSE_CODE_DETAILS"   ,"RESPONSE_CODE_DETAILS"     , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn CONN_TERM_DETAILS     = new LogEntryColumn("CONN_TERM_DETAILS"       ,"CONN_TERM_DETAILS"         , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn US_TRANS_FAIL_REASON  = new LogEntryColumn("US_TRANS_FAIL_REASON"    ,"US_TRANS_FAIL_REASON"      , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn BYTES_RECEIVED        = new LogEntryColumn("BYTES_RECEIVED"          ,"BYTES_RECEIVED"            , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn BYTES_SENT            = new LogEntryColumn("BYTES_SENT"              ,"BYTES_SENT"                , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn DURATION              = new LogEntryColumn("DURATION"                ,"DURATION"                  , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn RESP_X_E_US_SERV_TIME = new LogEntryColumn("RESP_X_E_US_SERV_TIME"   ,"RESP_X_E_US_SERV_TIME"     , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQ_X_FORWARDED_FOR   = new LogEntryColumn("REQ_X_FORWARDED_FOR"     ,"REQ_X_FORWARDED_FOR"       , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQ_USER_AGENT        = new LogEntryColumn("REQ_USER_AGENT"          ,"REQ_USER_AGENT"            , 160 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQ_X_REQUEST_ID      = new LogEntryColumn("REQ_X_REQUEST_ID"        ,"REQ_X_REQUEST_ID"          , 160 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQ_AUTHORITY         = new LogEntryColumn("REQ_AUTHORITY"           ,"REQ_AUTHORITY"             , 160 , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn US_HOST               = new LogEntryColumn("US_HOST"                 ,"US_HOST"                   , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn US_CLUSTER            = new LogEntryColumn("US_CLUSTER"              ,"US_CLUSTER"                , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn US_LOCAL_ADDRESS      = new LogEntryColumn("US_LOCAL_ADDRESS"        ,"US_LOCAL_ADDRESS"          , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn DS_LOCAL_ADDRESS      = new LogEntryColumn("DS_LOCAL_ADDRESS"        ,"DS_LOCAL_ADDRESS"          , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn DS_REMOTE_ADDRESS     = new LogEntryColumn("DS_REMOTE_ADDRESS"       ,"DS_REMOTE_ADDRESS"         , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQUESTED_SERVER_NAME = new LogEntryColumn("REQUESTED_SERVER_NAME"   ,"REQUESTED_SERVER_NAME"     , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn ROUTE_NAME            = new LogEntryColumn("ROUTE_NAME"              ,"ROUTE_NAME"                , 70  , SwingConstants.CENTER , true  , false );

    public static final LogEntryColumn REMOTE_HOST           = new LogEntryColumn("REMOTE_HOST"             ,"REMOTE_HOST"               , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REMOTE_USERNAME       = new LogEntryColumn("REMOTE_USERNAME"         ,"REMOTE_USERNAME"           , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REMOTE_USER           = new LogEntryColumn("REMOTE_USER"             ,"REMOTE_USER"               , 70  , SwingConstants.CENTER , true  , false );
    public static final LogEntryColumn REQ_THREAD_NAME       = new LogEntryColumn("REQ_THREAD_NAME"         ,"REQ_THREAD_NAME"           , 70  , SwingConstants.CENTER , true  , false );

    public static final LogEntryColumn POD_NAME              = new LogEntryColumn("POD_NAME"                ,"POD_NAME"                  , 100 , SwingConstants.CENTER , true  , true );

    // futureX keys are placeholder
    // CHECKSTYLE:ON
    // @formatter:on

    private static List<LogEntryColumn> logEntryColumnList;

    static {
        logEntryColumnList = new ArrayList<>();

        logEntryColumnList.add(ALERTNUMBER);
        logEntryColumnList.add(APP);
        logEntryColumnList.add(INTERACTIONNUMBER);
        logEntryColumnList.add(LASTINPUT);
        logEntryColumnList.add(LASTSTEP);
        logEntryColumnList.add(LEVEL);
        logEntryColumnList.add(LINE);
        logEntryColumnList.add(LOGGER);
        logEntryColumnList.add(MESSAGE);
        logEntryColumnList.add(MESSAGEID);
        logEntryColumnList.add(NODEID);
        logEntryColumnList.add(OBSERVEDKPI);
        logEntryColumnList.add(PEGATHREAD);
        logEntryColumnList.add(REQUESTORID);
        logEntryColumnList.add(STACK);
        logEntryColumnList.add(TENANTID);
        logEntryColumnList.add(THREAD);
        logEntryColumnList.add(THRESHOLDKPI);
        logEntryColumnList.add(TIMESTAMP);
        logEntryColumnList.add(USERID);
        logEntryColumnList.add(ALERTTYPE);
        logEntryColumnList.add(AUDITCORRELATION1);
        logEntryColumnList.add(AUDITCORRELATION2);
        logEntryColumnList.add(AUDITEVENT);
        logEntryColumnList.add(AUDITKEY);
        logEntryColumnList.add(AUDITNAME);
        logEntryColumnList.add(AUDITRULESET);
        logEntryColumnList.add(AUDITRULESETVERSION);
        logEntryColumnList.add(AUDITTYPE);
        logEntryColumnList.add(CLASS);
        logEntryColumnList.add(ENCODEDRULESET);
        logEntryColumnList.add(FILE);
        logEntryColumnList.add(FIRSTACTIVITY);
        logEntryColumnList.add(INSTANCE_ID);
        logEntryColumnList.add(LOCATIONINFO);
        logEntryColumnList.add(LOG4JID);
        logEntryColumnList.add(METHOD);
        logEntryColumnList.add(NDC);
        logEntryColumnList.add(PALDATA);
        logEntryColumnList.add(PARAMETERPAGEDATA);
        logEntryColumnList.add(PERSONALRULESETYN);
        logEntryColumnList.add(PRIMARYPAGECLASS);
        logEntryColumnList.add(PRIMARYPAGENAME);
        logEntryColumnList.add(PRSTACKTRACE);
        logEntryColumnList.add(RELATIVETIME);
        logEntryColumnList.add(RULEAPPNAMEANDVERSION);
        logEntryColumnList.add(STEPPAGECLASS);
        logEntryColumnList.add(STEPPAGENAME);
        logEntryColumnList.add(TENANTIDHASH);
        logEntryColumnList.add(TRACELIST);
        logEntryColumnList.add(VERSION);
        logEntryColumnList.add(WORKPOOL);
        logEntryColumnList.add(CORRELATIONID);
        logEntryColumnList.add(CLIENTPAGELOADID);
        logEntryColumnList.add(ISSTATELESSAPP);
        logEntryColumnList.add(CLIENTREQUESTID);
        logEntryColumnList.add(FUTURE3);

        logEntryColumnList.add(EVENT_TYPE);
        logEntryColumnList.add(SENDER_NODE_ID);
        logEntryColumnList.add(RUN_ID);
        logEntryColumnList.add(ORIGINATOR);
        logEntryColumnList.add(REASON);
        logEntryColumnList.add(PARTITION_STATUS);
        logEntryColumnList.add(PREVIOUS_STATUS);
        logEntryColumnList.add(INTENTION);
        logEntryColumnList.add(PARTITIONS);
        logEntryColumnList.add(THREAD_NAME);
        logEntryColumnList.add(EVENT);

        logEntryColumnList.add(START_TIME);
        logEntryColumnList.add(REQ_PATH);
        logEntryColumnList.add(RESPONSE_CODE);
        logEntryColumnList.add(RESPONSE_FLAGS);
        logEntryColumnList.add(RESPONSE_CODE_DETAILS);
        logEntryColumnList.add(CONN_TERM_DETAILS);
        logEntryColumnList.add(US_TRANS_FAIL_REASON);
        logEntryColumnList.add(BYTES_RECEIVED);
        logEntryColumnList.add(BYTES_SENT);
        logEntryColumnList.add(DURATION);
        logEntryColumnList.add(RESP_X_E_US_SERV_TIME);
        logEntryColumnList.add(REQ_X_FORWARDED_FOR);
        logEntryColumnList.add(REQ_USER_AGENT);
        logEntryColumnList.add(REQ_X_REQUEST_ID);
        logEntryColumnList.add(REQ_AUTHORITY);
        logEntryColumnList.add(US_HOST);
        logEntryColumnList.add(US_CLUSTER);
        logEntryColumnList.add(US_LOCAL_ADDRESS);
        logEntryColumnList.add(DS_LOCAL_ADDRESS);
        logEntryColumnList.add(DS_REMOTE_ADDRESS);
        logEntryColumnList.add(REQUESTED_SERVER_NAME);
        logEntryColumnList.add(ROUTE_NAME);

        logEntryColumnList.add(REMOTE_HOST);
        logEntryColumnList.add(REMOTE_USERNAME);
        logEntryColumnList.add(REMOTE_USER);
        logEntryColumnList.add(REQ_THREAD_NAME);
    }

    public LogEntryColumn(String columnId, String displayName, int prefColumnWidth, int horizontalAlignment,
            boolean visibleColumn, boolean filterable) {

        super(columnId, displayName, prefColumnWidth, horizontalAlignment, visibleColumn, filterable);
    }

    private static List<LogEntryColumn> getTableColumnList() {
        return logEntryColumnList;
    }

    public static LogEntryColumn getTableColumnById(String columnId) {

        LogEntryColumn tableColumn = DefaultTableColumn.getTableColumnById(columnId, getTableColumnList());

        return tableColumn;
    }

    public static List<LogEntryColumn> getCloudKPegaRulesColumnList() {

        List<LogEntryColumn> cloudKLogEventColumnList = new ArrayList<>();

        cloudKLogEventColumnList.add(LINE);
        cloudKLogEventColumnList.add(TIMESTAMP);
        cloudKLogEventColumnList.add(THREAD);
        cloudKLogEventColumnList.add(PEGATHREAD);
        cloudKLogEventColumnList.add(TENANTID);
        cloudKLogEventColumnList.add(APP);
        cloudKLogEventColumnList.add(LOGGER);
        cloudKLogEventColumnList.add(LEVEL);
        cloudKLogEventColumnList.add(STACK);
        cloudKLogEventColumnList.add(REQUESTORID);
        cloudKLogEventColumnList.add(CORRELATIONID);
        cloudKLogEventColumnList.add(USERID);
        cloudKLogEventColumnList.add(MESSAGE);

        return cloudKLogEventColumnList;
    }

    public static List<LogEntryColumn> getCloudKPegaClusterColumnList() {

        List<LogEntryColumn> cloudKLogEventColumnList = new ArrayList<>();

        cloudKLogEventColumnList.add(LINE);
        cloudKLogEventColumnList.add(TIMESTAMP);
        cloudKLogEventColumnList.add(THREAD);
        cloudKLogEventColumnList.add(LOGGER);
        cloudKLogEventColumnList.add(LEVEL);
        cloudKLogEventColumnList.add(MESSAGE);

        return cloudKLogEventColumnList;
    }

    public static List<LogEntryColumn> getCloudKPegaDataflowColumnList() {

        List<LogEntryColumn> cloudKLogEventColumnList = new ArrayList<>();

        cloudKLogEventColumnList.add(LINE);
        cloudKLogEventColumnList.add(TIMESTAMP);
        cloudKLogEventColumnList.add(THREAD);
        cloudKLogEventColumnList.add(PEGATHREAD);
        cloudKLogEventColumnList.add(LOGGER);
        cloudKLogEventColumnList.add(LEVEL);
        cloudKLogEventColumnList.add(CORRELATIONID);
        cloudKLogEventColumnList.add(MESSAGE);

        return cloudKLogEventColumnList;
    }

    public static List<LogEntryColumn> getDataflowLogEventColumnList() {

        List<LogEntryColumn> dataflowLogEventColumnList = new ArrayList<>();

        dataflowLogEventColumnList.add(MESSAGEID);
        dataflowLogEventColumnList.add(EVENT_TYPE);
        dataflowLogEventColumnList.add(SENDER_NODE_ID);
        dataflowLogEventColumnList.add(RUN_ID);
        dataflowLogEventColumnList.add(ORIGINATOR);
        dataflowLogEventColumnList.add(REASON);
        dataflowLogEventColumnList.add(PARTITION_STATUS);
        dataflowLogEventColumnList.add(PREVIOUS_STATUS);
        dataflowLogEventColumnList.add(INTENTION);
        dataflowLogEventColumnList.add(PARTITIONS);
        dataflowLogEventColumnList.add(THREAD_NAME);
        dataflowLogEventColumnList.add(EVENT);

        return dataflowLogEventColumnList;
    }

    public static List<LogEntryColumn> getLifeCycleEventColumnList() {

        List<LogEntryColumn> lifeCycleEventColumnList = new ArrayList<>();

        lifeCycleEventColumnList.add(MESSAGEID);
        lifeCycleEventColumnList.add(TIMESTAMP);
        lifeCycleEventColumnList.add(EVENT_TYPE);
        lifeCycleEventColumnList.add(SENDER_NODE_ID);
        lifeCycleEventColumnList.add(RUN_ID);
        lifeCycleEventColumnList.add(ORIGINATOR);
        lifeCycleEventColumnList.add(REASON);
        lifeCycleEventColumnList.add(PARTITION_STATUS);
        lifeCycleEventColumnList.add(PREVIOUS_STATUS);
        lifeCycleEventColumnList.add(INTENTION);
        lifeCycleEventColumnList.add(PARTITIONS);
        lifeCycleEventColumnList.add(THREAD_NAME);
        lifeCycleEventColumnList.add(EVENT);

        return lifeCycleEventColumnList;
    }

    public static List<LogEntryColumn> getCloudKIstioLogColumnList() {

        List<LogEntryColumn> logEntryColumnList = new ArrayList<>();

        logEntryColumnList.add(TIMESTAMP);
        logEntryColumnList.add(START_TIME);
        logEntryColumnList.add(REQ_PATH);
        logEntryColumnList.add(RESPONSE_CODE);
        logEntryColumnList.add(RESPONSE_FLAGS);
        logEntryColumnList.add(RESPONSE_CODE_DETAILS);
        logEntryColumnList.add(CONN_TERM_DETAILS);
        logEntryColumnList.add(US_TRANS_FAIL_REASON);
        logEntryColumnList.add(BYTES_RECEIVED);
        logEntryColumnList.add(BYTES_SENT);
        logEntryColumnList.add(DURATION);
        logEntryColumnList.add(RESP_X_E_US_SERV_TIME);
        logEntryColumnList.add(REQ_X_FORWARDED_FOR);
        logEntryColumnList.add(REQ_USER_AGENT);
        logEntryColumnList.add(REQ_X_REQUEST_ID);
        logEntryColumnList.add(REQ_AUTHORITY);
        logEntryColumnList.add(US_HOST);
        logEntryColumnList.add(US_CLUSTER);
        logEntryColumnList.add(US_LOCAL_ADDRESS);
        logEntryColumnList.add(DS_LOCAL_ADDRESS);
        logEntryColumnList.add(DS_REMOTE_ADDRESS);
        logEntryColumnList.add(REQUESTED_SERVER_NAME);
        logEntryColumnList.add(ROUTE_NAME);

        return logEntryColumnList;
    }

    public static List<LogEntryColumn> getGenericCloudKLogColumnList() {

        List<LogEntryColumn> genericCloudKLogColumnList = new ArrayList<>();

        genericCloudKLogColumnList.add(LINE);
        genericCloudKLogColumnList.add(TIMESTAMP);
        genericCloudKLogColumnList.add(MESSAGE);

        return genericCloudKLogColumnList;
    }
}
