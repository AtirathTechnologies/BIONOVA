package com.bionova.service;

import com.bionova.entity.ActivityLog;
import com.bionova.entity.Employee;
import com.bionova.entity.MilestoneLive;
import com.bionova.entity.ProjectLive;
import com.bionova.entity.TaskLive;
import com.bionova.entity.AppNotification;
import com.bionova.repository.ActivityLogRepository;
import com.bionova.repository.EmployeeRepository;
import com.bionova.repository.MilestoneLiveRepository;
import com.bionova.repository.ProjectLiveRepository;
import com.bionova.repository.TaskLiveRepository;
import com.bionova.repository.AppNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private TaskLiveRepository taskLiveRepository;

    @Autowired
    private MilestoneLiveRepository milestoneLiveRepository;

    @Autowired
    private ProjectLiveRepository projectLiveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AppNotificationRepository appNotificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Scheduled(fixedDelay = 10000) // Runs every 10 seconds
    @Transactional
    public void processUnprocessedLogs() {
        List<ActivityLog> unprocessedLogs = activityLogRepository.findByProcessedFalse();
        if (unprocessedLogs.isEmpty()) {
            return;
        }

        System.out.println("Processing " + unprocessedLogs.size() + " status change logs for notifications...");

        for (ActivityLog log : unprocessedLogs) {
            try {
                boolean sent = false;
                if ("TASK".equalsIgnoreCase(log.getEntityTyp())) {
                    sent = sendTaskNotification(log);
                } else if ("MILESTONE".equalsIgnoreCase(log.getEntityTyp())) {
                    sent = sendMilestoneNotification(log);
                } else if ("PROJECT".equalsIgnoreCase(log.getEntityTyp())) {
                    sent = sendProjectNotification(log);
                }

                // Mark as processed regardless of whether email was sent (e.g. if entities are deleted/missing, we don't want to get stuck in infinite loop)
                log.setProcessed(true);
                activityLogRepository.save(log);
            } catch (Exception e) {
                System.err.println("Error processing notification for Log ID " + log.getLogId() + ": " + e.getMessage());
            }
        }
    }

    private boolean sendTaskNotification(ActivityLog log) {
        TaskLive task = taskLiveRepository.findById(log.getEntityId()).orElse(null);
        if (task == null) {
            System.out.println("Task with ID " + log.getEntityId() + " not found. Skipping notification.");
            return false;
        }

        Employee employee = (task.getEmpId() != null) ? employeeRepository.findById(task.getEmpId()).orElse(null) : null;
        String recipientEmail = (employee != null) ? employee.getEmail() : "siva@atirath.com";
        String recipientName = (employee != null) ? employee.getFirstName() + " " + employee.getLastName() : "Admin";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(recipientEmail);
        message.setSubject("BIONOVA – Task Status Update: " + task.getTaskCd());

        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(recipientName).append(",\n\n");
        body.append("There has been a status update for the task assigned to you:\n\n");
        body.append("Task Code: ").append(task.getTaskCd()).append("\n");
        body.append("Task Name: ").append(task.getTaskNm()).append("\n");
        body.append("Status Changed: ").append(log.getStatusFrom()).append(" ➜ ").append(log.getStatusTo()).append("\n");
        body.append("Deadline (End Date): ").append(task.getEndDt()).append("\n\n");

        LocalDate today = LocalDate.now();
        boolean isDelayed = false;
        if ("COMPLETED".equalsIgnoreCase(log.getStatusTo())) {
            if (task.getEndDt() != null) {
                if (today.isAfter(task.getEndDt())) {
                    isDelayed = true;
                    body.append("⚠️ Note: This task was completed with a DELAY. The deadline was ").append(task.getEndDt()).append(".\n\n");
                } else {
                    body.append("🎉 Congratulations! This task was completed ON TIME / AHEAD OF SCHEDULE (Lead Time).\n\n");
                }
            }
        } else {
            // Warn if task is delayed in another status
            if (task.getEndDt() != null && today.isAfter(task.getEndDt())) {
                isDelayed = true;
                body.append("⚠️ Warning: This task has EXCEEDED its deadline of ").append(task.getEndDt()).append(". Current status is: ").append(log.getStatusTo()).append(". Please update or complete it as soon as possible.\n\n");
            }
        }

        body.append("Thank you,\nBIONOVA Team");

        message.setText(body.toString());
        mailSender.send(message);
        System.out.println("Sent task status update notification to: " + recipientEmail);

        // Save App Notification for Supabase Realtime Broadcast
        AppNotification appNotification = new AppNotification();
        appNotification.setEmpId(employee != null ? employee.getEmpId() : null);
        appNotification.setTitle("Task Update: " + task.getTaskCd());
        appNotification.setMessage("Task '" + task.getTaskNm() + "' status changed from " + log.getStatusFrom() + " to " + log.getStatusTo()
                + (isDelayed ? " (⚠️ DELAYED)" : ""));
        appNotificationRepository.save(appNotification);
        System.out.println("Saved AppNotification for employee ID: " + (employee != null ? employee.getEmpId() : null));

        return true;
    }

    private boolean sendMilestoneNotification(ActivityLog log) {
        MilestoneLive ms = milestoneLiveRepository.findById(log.getEntityId()).orElse(null);
        if (ms == null) {
            System.out.println("Milestone with ID " + log.getEntityId() + " not found. Skipping notification.");
            return false;
        }

        ProjectLive project = projectLiveRepository.findById(ms.getPrjId()).orElse(null);
        String prjName = (project != null) ? project.getPrjNm() : "Unknown Project";

        // Milestones status updates go to Project Manager / Admin
        String recipientEmail = "siva@atirath.com";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(recipientEmail);
        message.setSubject("BIONOVA – Milestone Status Update: Project " + prjName);

        StringBuilder body = new StringBuilder();
        body.append("Hello Manager,\n\n");
        body.append("A milestone has been updated in project '").append(prjName).append("':\n\n");
        body.append("Milestone Name: ").append(ms.getMlstnTtl()).append("\n");
        body.append("Status Changed: ").append(log.getStatusFrom()).append(" ➜ ").append(log.getStatusTo()).append("\n");
        body.append("Target End Date: ").append(ms.getEndDt()).append("\n\n");

        LocalDate today = LocalDate.now();
        boolean isDelayed = false;
        if ("COMPLETED".equalsIgnoreCase(log.getStatusTo()) || "CLOSED".equalsIgnoreCase(log.getStatusTo())) {
            if (ms.getEndDt() != null) {
                if (today.isAfter(ms.getEndDt())) {
                    isDelayed = true;
                    body.append("⚠️ Note: This milestone was completed with a DELAY. The deadline was ").append(ms.getEndDt()).append(".\n\n");
                } else {
                    body.append("🎉 Success: This milestone was completed ON TIME / AHEAD OF SCHEDULE.\n\n");
                }
            }
        }

        body.append("Thank you,\nBIONOVA Team");

        message.setText(body.toString());
        mailSender.send(message);
        System.out.println("Sent milestone status update notification to: " + recipientEmail);

        // Save App Notification for Manager/Admin
        Employee manager = employeeRepository.findByEmail("siva@atirath.com").orElse(null);
        AppNotification appNotification = new AppNotification();
        appNotification.setEmpId(manager != null ? manager.getEmpId() : null);
        appNotification.setTitle("Milestone Update: " + ms.getMlstnCd());
        appNotification.setMessage("Milestone '" + ms.getMlstnTtl() + "' status changed from " + log.getStatusFrom() + " to " + log.getStatusTo()
                + (isDelayed ? " (⚠️ DELAYED)" : ""));
        appNotificationRepository.save(appNotification);

        return true;
    }

    private boolean sendProjectNotification(ActivityLog log) {
        ProjectLive project = projectLiveRepository.findById(log.getEntityId()).orElse(null);
        if (project == null) {
            System.out.println("Project with ID " + log.getEntityId() + " not found. Skipping notification.");
            return false;
        }

        // Project status updates go to Project Admin
        String recipientEmail = "siva@atirath.com";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(recipientEmail);
        message.setSubject("BIONOVA – Project Status Update: " + project.getPrjNm());

        StringBuilder body = new StringBuilder();
        body.append("Hello Manager,\n\n");
        body.append("The status of the project has changed:\n\n");
        body.append("Project Code: ").append(project.getPrjCd()).append("\n");
        body.append("Project Name: ").append(project.getPrjNm()).append("\n");
        body.append("Status Changed: ").append(log.getStatusFrom()).append(" ➜ ").append(log.getStatusTo()).append("\n");
        body.append("Target End Date: ").append(project.getEndDt()).append("\n\n");

        LocalDate today = LocalDate.now();
        boolean isDelayed = false;
        if ("CLOSED".equalsIgnoreCase(log.getStatusTo())) {
            if (project.getEndDt() != null) {
                if (today.isAfter(project.getEndDt())) {
                    isDelayed = true;
                    body.append("⚠️ Note: The project was closed with a DELAY. Target end date was ").append(project.getEndDt()).append(".\n\n");
                } else {
                    body.append("🎉 Success: The project has been successfully completed and closed ON TIME.\n\n");
                }
            }
        }

        body.append("Thank you,\nBIONOVA Team");

        message.setText(body.toString());
        mailSender.send(message);
        System.out.println("Sent project status update notification to: " + recipientEmail);

        // Save App Notification for Manager/Admin
        Employee manager = employeeRepository.findByEmail("siva@atirath.com").orElse(null);
        AppNotification appNotification = new AppNotification();
        appNotification.setEmpId(manager != null ? manager.getEmpId() : null);
        appNotification.setTitle("Project Update: " + project.getPrjNm());
        appNotification.setMessage("Project status changed from " + log.getStatusFrom() + " to " + log.getStatusTo()
                + (isDelayed ? " (⚠️ DELAYED)" : ""));
        appNotificationRepository.save(appNotification);

        return true;
    }
}
