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
Map<String,String> currentTask =[:]
currentTaskid = rtTaksget.getId()
echo "currentTaskid value is : ${currentTaskid}"
currentTaskName = rtTaks.getName()
echo "currentTaskName : ${currentTaskName}"
currentTaskType = rtTaks.getSchedule()
echo "currentTaskType : ${currentTaskType}"
currentTaskConfiguration = rtTaks.getConfiguration()
echo "currentTaskConfiguration : ${currentTaskConfiguration}"
currentTaskMail = currentTaskConfiguration.getAlertEmail()
if(currentTaskType == "daily") {

}
if(currentTaskType == "now") {

}
if(currentTaskType == "") {

}
if(currentTaskType == "once") {

}
if(currentTaskType == "hourly") {

}
if(currentTaskType == "weekly") {

}
if(currentTaskType == "monthly") {

}

if(currentTaskid == "repository.maven.purge-unused-snapshots") {
}
    
}

