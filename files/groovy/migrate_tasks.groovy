import groovy.json.JsonOutput
import org.sonatype.nexus.scheduling.TaskConfiguration
import org.sonatype.nexus.scheduling.TaskInfo
import org.sonatype.nexus.scheduling.TaskScheduler
import org.sonatype.nexus.scheduling.schedule.Monthly
import org.sonatype.nexus.scheduling.schedule.Schedule
import org.sonatype.nexus.scheduling.schedule.Weekly
import java.text.SimpleDateFormat
import java.util.*;

def fileName = 'tasks.yaml'
def migrationTasks = ['nexus_scheduled_tasks':[]]
Map scriptResults = [changed: false, error: false, 'action_details': [:]]
TaskScheduler taskScheduler = container.lookup(TaskScheduler.class.getName())
List<TaskInfo> existingTask = taskScheduler.listsTasks()
existingTask.each { rtTaks ->
def curentTaskProperty = []
Map<String,String> currentTask =[:]
Schedule currentTaskScheduleType = rtTaks.getSchedule()

scheduleType = currentTaskScheduleType.getType()
if(scheduleType == 'daily' || scheduleType== 'hourly' || scheduleType == 'once'){
    currentTask.put('schedule_type',scheduleType)
    schedule_startAt = currentTaskScheduleType.getStartAt()
    currentTask.put('schedule_startAt',schedule_startAt)
}
if(scheduleType == 'weekly'){
    currentTask.put('schedule_type',scheduleType)
    weekly_days = currentTaskScheduleType.getDaysToRun()
    currentTask.put('weekly_days',weekly_days)
}
if(scheduleType == 'monthly'){
    currentTask.put('schedule_type',scheduleType)
    monthly_days = currentTaskScheduleType.getDaysToRun()
    def mDay =[]
    monthly_days.each { dayVal ->
        mDay.add(dayVal['day'])
    }
    currentTask.put('monthly_days',mDay)
}
if(scheduleType == 'cron'){
    schedule_cronExpression = currentTaskScheduleType.getCronExpression()
    currentTask.put('cron',schedule_cronExpression.toString())
}
if(scheduleType == 'manual'){
    currentTask.put('schedule_type',scheduleType)
}
if(scheduleType =='now'){
    currentTask.put('schedule_type',scheduleType)
}
TaskConfiguration currentTaskConfiguration = rtTaks.getConfiguration()

tasksName = currentTaskConfiguration.getName()
// tasksId = currentTaskConfiguration.getId() not requried 

String tasksTypeId = currentTaskConfiguration.getTypeId()
String tasksTypeName = currentTaskConfiguration.getTypeName()
String tasksAleartMail = currentTaskConfiguration.getAlertEmail()
String tasksNotification = currentTaskConfiguration.getNotificationCondition()
String tasksRepository = currentTaskConfiguration.getString('repositoryName')
String tasksblobstoreName = currentTaskConfiguration.getString('blobstoreName')
String tasklastUsed = currentTaskConfiguration.getString('lastUsed')
String tasksgroupId = currentTaskConfiguration.getString('groupId')
String tasksartifactId = currentTaskConfiguration.getString('artifactId')
String taskbaseVersion = currentTaskConfiguration.getString('baseVersion')
String tasksrebuildChecksums = currentTaskConfiguration.getString('rebuildChecksums')
String taskyumMetadataCaching = currentTaskConfiguration.getString('yumMetadataCaching')
String tasklocation = currentTaskConfiguration.getString('location')
String tasksAge = currentTaskConfiguration.getString('age')
String taskSource = currentTaskConfiguration.getString('source')
String taskDryrun = currentTaskConfiguration.getString('dryRun')
String taskrestoreBlobMetadata = currentTaskConfiguration.getString('restoreBlobMetadata')
String taskunDeleteReferencedBlobs=currentTaskConfiguration.getString('unDeleteReferencedBlobs')
String taskintegrityCheck=currentTaskConfiguration.getString('integrityCheck')
String taskLanguage = currentTaskConfiguration.getString('language')


def taskproperty = [:]
def boolproperty = [:]
if(tasksTypeId =='epository.docker.upload-purge'){
    taskproperty.put('age',tasksAge)
}
if(tasksTypeId == 'repository.maven.remove-snapshots'){
    taskproperty.put('minimumRetained',currentTaskConfiguration.getString('minimumRetained'))
    //taskproperty.put('minimumRetained',currentTaskConfiguration.getString('integrityCheck'))
    taskproperty.put('snapshotRetentionDays',currentTaskConfiguration.getString('snapshotRetentionDays'))
    taskproperty.put('gracePeriodInDays',currentTaskConfiguration.getString('gracePeriodInDays'))
    boolproperty.put('removeIfReleased',currentTaskConfiguration.getString('removeIfReleased'))
}
if(tasksTypeId == 'blobstore.compact' || tasksTypeId =='security.purge-api-keys' || tasksTypeId=='blobstore.rebuildComponentDB'){
    taskproperty.put('blobstoreName',tasksblobstoreName)
}
if(tasksTypeId == 'repository.maven.purge-unused-snapshots' || tasksTypeId =='repository.purge-unused') {
    taskproperty.put('lastUsed',tasklastUsed)
}
if(tasksTypeId =='script'){
    taskproperty.put('source',taskSource)
    taskproperty.put('language',taskLanguage)
}
if(tasksTypeId == 'repository.maven.rebuild-metadata'){
    taskproperty.put('groupId',tasksgroupId)
    taskproperty.put('artifactId',tasksartifactId)
    taskproperty.put('baseVersion',taskbaseVersion)
    taskproperty.put('rebuildChecksums',tasksrebuildChecksums)
}
if(tasksTypeId == 'repository.yum.rebuild.metadata'){
    boolproperty.put('yumMetadataCaching',taskyumMetadataCaching)
}
if(tasksTypeId == 'db.backup'){
taskproperty.put('location',tasklocation)
}
if(tasksTypeId=='rebuild.asset.uploadMetadata'){
    boolproperty.put('dryRun',taskDryrun)
    boolproperty.put('restoreBlobMetadata',taskrestoreBlobMetadata)
    boolproperty.put('unDeleteReferencedBlobs',taskunDeleteReferencedBlobs)
    boolproperty.put('integrityCheck',taskintegrityCheck)
}

currentTask.put('name',tasksName)
currentTask.put('typeId',tasksTypeId)
currentTask.put('task_alert_email',tasksAleartMail)
currentTask.put('notificationCondition',tasksNotification)
if(tasksTypeId != 'blobstore.compact' && tasksTypeId!='repository.storage-facet-cleanup' && tasksTypeId!='repository.docker.upload-purge' && tasksTypeId!='rebuild.asset.uploadMetadata' && tasksTypeId!='blobstore.rebuildComponentDB' && tasksTypeId!='db.backup' && tasksTypeId!='script'){
    taskproperty.put('repositoryName',tasksRepository)
}
if(! boolproperty.isEmpty()){
    currentTask.put('booleanTaskProperties',boolproperty)
}
if(! taskproperty.isEmpty()){
currentTask.put('taskProperties',taskproperty)
}
migrationTasks['nexus_scheduled_tasks'].add(currentTask)
}
scriptResults['action_details'].put(fileName, migrationTasks)
return JsonOutput.toJson(scriptResults)
