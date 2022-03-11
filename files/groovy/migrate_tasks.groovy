import groovy.json.JsonSlurper
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
TaskInfo existingTask = taskScheduler.listsTasks()
if(existingTask) {
    return
}
existingTask.each { rtTaks ->
def curentTaskProperty = []
Map<String,String> currentTask =[:]
Map<String,String> currentTaskProperties =[:]
Map<String,String> currentBooleanTaskProperties =[:]

/*
Below code line 25 --- to --- 43 is responsible to calculate task schedule type and date & time 
store the schedule type in Map currentTask
*/
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
//-----------------------------*-------------------------------------------//
/*
Blow code is responsible to calculate tast configuration details 
*/

TaskConfiguration currentTaskConfiguration = rtTaks.getConfiguration()
tasksName = currentTaskConfiguration.getName()
tasksId = currentTaskConfiguration.getId()

tasksTypeId = currentTaskConfiguration.getTypeId()
tasksTypeName = currentTaskConfiguration.getTypeName()
tasksAleartMail = currentTaskConfiguration.getAlertEmail()
tasksNotification = currentTaskConfiguration.getNotificationCondition()
blobstoreName = getblobstoreName()
repositoryName = getRepositoryName()
Map<String,String> taskproperty
Map<String,Boolean> booleantaskproperty

if(repositoryName !=null && tasksTypeId != 'blobstore.compac'){
    taskproperty['repositoryName'] = repositoryName
}
if (tasksTypeId == 'blobstore.compact' || 'blobstore.rebuildComponentDB') {
    if(!blobstoreName.empty() || blobstoreName !=null){  
        taskproperty['blobstoreName']=blobstoreName
    }
}
if (tasksTypeId == 'repository.maven.purge-unused-snapshots') {
    lastUsed = ''
    taskproperty['lastUsed'] = lastUsed 
}
if (tasksTypeId == 'repository.maven.rebuild-metadata') {
    groupId = ''
    artifactId = ''
    baseVersion = ''
    rebuildChecksums = true
    taskproperty['groupId'] = groupId
    taskproperty['artifactId']= artifactId
    taskproperty['basrVersion']=baseVersion
    booleantaskproperty['rebuildChecksums']=rebuildChecksums
}
if (tasksTypeId == 'repository.yum.rebuild.metadata'){
    yumMetadataCaching: true
    booleantaskproperty['yumMetadataCaching']=yumMetadataCaching
}
if(tasksTypeId == 'rebuild.asset.uploadMetadata') {
    dryRun = true
    restoreBlobMetadata = true
    unDeleteReferencedBlobs = true
    integrityCheck = true
    booleantaskproperty['dryRun']=dryRun
    booleantaskproperty['restoreBlobMetadata']=restoreBlobMetadata
    booleantaskproperty['unDeleteReferencedBlobs']=unDeleteReferencedBlobs
    booleantaskproperty['integrityCheck']=integrityCheck
}
if(tasksTypeId == 'db.backup') {
    location = ''
    taskproperty['location']=location
}
//adding all in one variable and parsing it to json 
curentTaskProperty['taskProperties'].add(taskproperty)
curentTaskProperty['booleanTaskProperties'].add(booleanTaskProperties)

currentTask.put('name',currentTaskName)
currentTask.put('typeId',tasksTypeId)
currentTask.put('task_alert_email',currentTaskMail)
currentTask.put('notificationCondition',currentTaskNotification)
migrationTasks['nexus_scheduled_tasks'].add(currentTask)
migrationTasks['nexus_scheduled_tasks'].add(curentTaskProperty)
}
scriptResults['action_details'].put(fileName, migrationTasks)
return JsonOutput.toJson(scriptResults)


