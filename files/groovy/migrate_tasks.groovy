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
currentTaskId = rtTaks.getId()
currentTaskName = rtTaks.getName()
currentTaskType = rtTaks.getSchedule()
currentTaskConfiguration = rtTaks.getConfiguration()
currentTaskMail = rtTaks.getAlertEmail()
currentTaskNotification = rtTaks.getNotificationCondition()


currentTask.put('name',currentTaskName)
currentTask.put('id',currentTaskId)
currentTask.put('shedule_type',currentTaskType)
currentTask.put('alertEmail',currentTaskMail)
currentTask.put('configuration',currentTaskConfiguration)
currentTask.put('notificationCondition',currentTaskNotification)
migrationTasks['nexus_scheduled_tasks'].add(currentTask)

}
scriptResults['action_details'].put(fileName, migrationTasks)
return JsonOutput.toJson(scriptResults)


