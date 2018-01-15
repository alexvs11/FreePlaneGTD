import static org.quartz.TriggerBuilder.*
import static org.quartz.CronScheduleBuilder.*
import static org.quartz.DateBuilder.*
import org.quartz.CronExpression

import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

public class MyJob implements org.quartz.Job {
    public MyJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            def n = getNode(context)
            // TBD: some issues in array convertions in groovy script
            println "checking if it's time to unmark $n " + n.icons.icons[0]
            if (n.icons.icons[0].contains("button_ok")) {
               println "removing done for $n"
               n.icons[0].remove("button_ok")
            }
            println "exit"
        } catch(e) {
            println "caught $e"
        }
    }
    
    def getNode(context) { 
        def schedctx = context.getScheduler().getContext()
        def root = schedctx.get("root")
        def id = context.getMergedJobDataMap().getString("node")
        return root.find{ it.getId() == id }
    }
}
  
Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
scheduler.start();
  
JobDetail job = newJob(MyJob.class)
 .withIdentity("job2", "group2")
 .build();

Trigger trigger = newTrigger()
 .withIdentity("trigger2", "group2")
 .startNow()
 .usingJobData("node",node.getId())
 .withSchedule(cronSchedule("0 * * * * ?")).build();

scheduler.getContext().put("root", node.map.root);
scheduler.scheduleJob(job, trigger);
