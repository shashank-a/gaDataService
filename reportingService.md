gaDataService
=============

Reporting Service
# AgentActionReport

This report is used by Performance Management Team and IT team and it is mailed on every day to their respective email groups. here is a sample report... https://drive.google.com/a/a-cti.com/file/d/0B9W6M_WJhCmwNWZFbkkxTk9KdnJMMFUtMHNCeUw4WGpGalFR/edit?usp=sharing

As of now there are about 80K records created in SB Google Analytics every working day. In order to manipulate these details we require app-engine backend which can be later upgraded to an app-engine module.

Agent Action Report is constructed and mailed using 3 crons on gadataservice.appspot.com and in following order:

http://gabackend.gadataservice.appspot.com/fetchBatchData.do?dateFrom=2014-05-26 Fetches data from GA API and stores data in batch of 1000 rows in datastore. Datastore key sample GaDataObject_SBLive_20140629_Cat|Act|Lab|cVal1|cVal2|cVal3|cVal4_Part_1||3 can be searched by dimension=’20140629’

http://gabackend.gadataservice.appspot.com/processAgentReport.do?dateFrom=2014-05-26

Merges data into one unit and iterates of whole data to give action count per agent. any action pushed to google analytics will be dynamically added to this report. Datastore key sample: AgentActionCount_CSV20140629, GaDataObject_SBLIVE_20140629_Cat|Act|Lab|cVal1|cVal2|cVal3|cVal4

http://gadataservice.appspot.com/agentActionEmailService.do?dateFrom=2014-05-26

This service does not require as the processed data is already present in the datastore.and can be mailed directly to marked email groups.

Note:

By default the cron service takes previous days date to fetch and construct report if no date is provide in the service URL.
Actual Crons are scheduled at an interval of 10 -15 mins in order to stabilize the batch data stored in datastore.
# V2 Outbound Report

All outbound calls are logged using the events recorded in V2 Analytics. We are currently providing Client Web Access Team daily outbound reports and Monthly outbound report to Finance Team.

http://gabackend.gadataservice.appspot.com/fetchV2Outbound.do?range=monthly&dateFrom=2014-07-01&dateTo=2014-07-01&email=shashank.ashokkumar%40a-cti.com

Above service fetches the batch data for given date range and emails the data to given email address. Above mentioned service is also scheduled through a cron. Datastore key sample:GaDataObject_V2Outbound_20140629_20140629

V2 Outbound Report Interface

http://gadataservice.appspot.com/pages/outboundReport.jsp

Analytics Query Explorer

Many other reports can be customized using the Google Analytics Query Explorer: http://analytics-demo-work.appspot.com/

This application uses local storage to store user profile. Any new addition to user's analytics profile will reflect after clearing local storage for the browser.
