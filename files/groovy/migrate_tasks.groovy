import groovy.json.JsonOutput
import org.sonatype.nexus.scheduling.TaskConfiguration
import org.sonatype.nexus.scheduling.TaskInfo
import org.sonatype.nexus.scheduling.TaskScheduler
import org.sonatype.nexus.scheduling.schedule.Monthly
import org.sonatype.nexus.scheduling.schedule.Schedule
import org.sonatype.nexus.scheduling.schedule.Weekly
import java.text.SimpleDateFormat

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
currentTask.put('shedule_type',scheduleType)
if(scheduleType == 'daily' || scheduleType== 'hourly' || scheduleType == 'once'){
    schedule_startAt = currentTaskScheduleType.getStartAt()
    currentTask.put('schedule_startAt',schedule_startAt)
}
if(scheduleType == 'weekly'){
    weekly_days = currentTaskScheduleType.getDaysToRun()
    currentTask.put('weekly_days',weekly_days)
}
if(scheduleType == 'monthly'){
    monthly_days = currentTaskScheduleType.getDaysToRun()
    currentTask.put('monthly_days',monthly_days)
}
if(scheduleType == 'cron'){
    schedule_cronExpression = currentTaskScheduleType.getCronExpression()
    currentTask.put('schedule_cronExpression',schedule_cronExpression)
}
TaskConfiguration currentTaskConfiguration = rtTaks.getConfiguration()

tasksName = currentTaskConfiguration.getName()
// tasksId = currentTaskConfiguration.getId() not requried 

tasksTypeId = currentTaskConfiguration.getTypeId()
tasksTypeName = currentTaskConfiguration.getTypeName()
tasksAleartMail = currentTaskConfiguration.getAlertEmail()
tasksNotification = currentTaskConfiguration.getNotificationCondition()
tasksRepository = currentTaskConfiguration.getString('repositoryName')
tasksblobstoreName = currentTaskConfiguration.getString('blobstoreName')
tasklastUsed = currentTaskConfiguration.getString('lastUsed')
tasksgroupId = currentTaskConfiguration.getString('groupId')
tasksartifactId = currentTaskConfiguration.getString('artifactId')
taskbaseVersion = currentTaskConfiguration.getString('baseVersion')
tasksrebuildChecksums = currentTaskConfiguration.getString('rebuildChecksums')
taskyumMetadataCaching = currentTaskConfiguration.getString('yumMetadataCaching')
tasklocation = currentTaskConfiguration.getString('location')
tasksAge = currentTaskConfiguration.getString('age')
taskSource = currentTaskConfiguration.getString('source')
taskDryrun = currentTaskConfiguration.getString('dryRun')
taskrestoreBlobMetadata = currentTaskConfiguration.getString('restoreBlobMetadata')
taskunDeleteReferencedBlobs=currentTaskConfiguration.getString('unDeleteReferencedBlobs')
taskintegrityCheck=currentTaskConfiguration.getString('integrityCheck')

def taskproperty = [:]
def boolproperty = [:]

if(tasksTypeId == 'blobstore.compact' || tasksTypeId =='security.purge-api-keys' || tasksTypeId=='blobstore.rebuildComponentDB'){
    taskproperty.put('blobstoreName',tasksblobstoreName)
}
if(tasksTypeId == 'repository.maven.purge-unused-snapshots') {
    taskproperty.put('lastUsed',tasklastUsed)
}
if(tasksTypeId =='script'){
    taskproperty.put('source',taskSource)
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
