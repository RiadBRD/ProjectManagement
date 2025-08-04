package com.project;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.project.entity.DataBaseConnectivity;
import com.project.entity.FileManagement;
import com.project.entity.Project;
import com.project.entity.Task;
import com.project.enums.ProjectStatus;
import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;
import com.project.exceptions.BusinessException;
import com.project.exceptions.DuplicateProjectException;
import com.project.exceptions.DuplicateTaskException;
import com.project.repository.ProjectRepository;
import com.project.repository.TaskRepository;
import com.project.services.ProjectService;
import com.project.services.ReminderService;
import com.project.services.TaskService;

public class ProjectManagementApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static ProjectService projectService;
    private static TaskService taskService;
    private static ReminderService reminderService;
    private static boolean remindersActive = false;

    public static void main(String[] args) {
        try {
            initializeServices();
            runApplication();
        } catch (Exception e) {
            System.err.println("Erreur critique: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private static void initializeServices() throws SQLException {
        System.out.println("Initialisation des services...");
        DataBaseConnectivity db = new DataBaseConnectivity();
        db.connect();

        projectService = new ProjectService(new ProjectRepository(db));
        taskService = new TaskService(new TaskRepository(db));
        reminderService = new ReminderService(taskService.getAllTasks());
        System.out.println("✅ Services initialisés avec succès");
    }

    private static void runApplication() throws SQLException {
        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            int choice = safeReadInt("Votre choix: ");

            switch (choice) {
                case 1 -> manageProjects();
                case 2 -> manageTasks();
                case 3 -> manageReminders();
                case 4 -> generateReports();
                case 0 -> exit = confirmExit();
                default -> System.out.println("❌ Option invalide!");
            }
        }
    }

    private static void cleanup() {
        if (remindersActive) {
            reminderService.stop();
        }
        scanner.close();
        System.out.println("👋 Application fermée");
    }

    // ==================== MENUS PRINCIPAUX ====================
    private static void displayMainMenu() {
        System.out.println("\n=== GESTION DE PROJETS ===");
        System.out.println("1. 📋 Gestion des projets");
        System.out.println("2. ✅ Gestion des tâches");
        System.out.println("3. ⏰ Gestion des rappels");
        System.out.println("4. 📊 Rapports et statistiques");
        System.out.println("0. 🚪 Quitter");
    }

    // ==================== GESTION PROJETS ====================
    private static void manageProjects() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- GESTION DES PROJETS ---");
            System.out.println("1. 🆕 Créer un projet");
            System.out.println("2. 📜 Lister tous les projets");
            System.out.println("3. 🔍 Rechercher un projet");
            System.out.println("4. ✏️ Modifier un projet");
            System.out.println("5. 🗑️ Supprimer un projet");
            System.out.println("6. 📌 Voir les tâches d'un projet");
            System.out.println("0. ↩️ Retour");

            int choice = safeReadInt("Choix: ");
            switch (choice) {
                case 1 -> createProject();
                case 2 -> listAllProjects(false);
                case 3 -> findProjectByName();
                case 4 -> updateProject();
                case 5 -> deleteProject();
                case 6 -> viewProjectTasks();
                case 0 -> back = true;
                default -> System.out.println("❌ Option invalide!");
            }
        }
    }

    private static void createProject() {
        System.out.print("Nom du projet: ");
        String name = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        try {
            Project project = projectService.createProject(name, description);
            System.out.println("✅ Projet créé: " + project.getName());
        } catch (DuplicateProjectException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur base de données: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Validation: " + e.getMessage());
        }
    }

    private static void listAllProjects(boolean brief) throws SQLException {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("ℹ️ Aucun projet trouvé");
            return;
        }

        System.out.println("\n📋 Liste des projets:");
        for (Project p : projects) {
            if (brief) {
                System.out.printf("- %s (Statut: %s, Progression: %.1f%%)\n",
                        p.getName(), p.getStatus(), p.getProgression());
            } else {
                System.out.println(p.toString());
            }
        }
    }

    private static void findProjectByName() {
        System.out.print("Nom du projet: ");
        String name = scanner.nextLine();

        Project project = projectService.findByName(name);
        if (project != null) {
            displayProjectDetails(project);
        } else {
            System.out.println("❌ Projet non trouvé");
        }
    }

    private static void displayProjectDetails(Project project) {
        System.out.println("\n📋 Détails du projet:");
        System.out.println("Nom: " + project.getName());
        System.out.println("Description: " + project.getDescription());
        System.out.println("Statut: " + project.getStatus());
        System.out.printf("Progression: %.1f%%\n", project.getProgression());
        System.out.println("Date début: " + project.getFrom());
        System.out.println("Date fin: " + (project.getTo() != null ? project.getTo() : "Non définie"));
    }

    private static void updateProject() throws SQLException {
        System.out.print("Nom du projet à modifier: ");
        String name = scanner.nextLine();

        Project project = projectService.findByName(name);
        if (project == null) {
            System.out.println("❌ Projet non trouvé");
            return;
        }

        System.out.print("Nouvelle description (laisser vide pour ne pas modifier): ");
        String newDesc = scanner.nextLine();
        
        System.out.print("Nouveau statut (TODO/IN_PROGRESS/DONE) (laisser vide pour ne pas modifier): ");
        String newStatus = scanner.nextLine();

        System.out.print("Nouvelle date de fin (AAAA-MM-JJ) (laisser vide pour ne pas modifier): ");
        String newEndDate = scanner.nextLine();

        if (!newDesc.isEmpty()) project.setDescription(newDesc);
        if (!newStatus.isEmpty()) project.setStatus(ProjectStatus.valueOf(newStatus));
        if (!newEndDate.isEmpty()) project.setTo(LocalDate.parse(newEndDate));

        boolean success = projectService.updateProject(project);
        System.out.println(success ? "✅ Projet mis à jour" : "❌ Échec de la mise à jour");
    }

    private static void deleteProject() throws SQLException {
        System.out.print("Nom du projet à supprimer: ");
        String name = scanner.nextLine();

        Project project = projectService.findByName(name);
        if (project == null) {
            System.out.println("❌ Projet non trouvé");
            return;
        }

        System.out.print("Confirmer la suppression (o/n)? ");
        if (scanner.nextLine().equalsIgnoreCase("o")) {
            boolean success = projectService.deleteProject(project);
            System.out.println(success ? "✅ Projet supprimé" : "❌ Échec de la suppression");
        }
    }

    private static void viewProjectTasks() {
        System.out.print("Nom du projet: ");
        String name = scanner.nextLine();

        Project project = projectService.findByName(name);
        if (project == null) {
            System.out.println("❌ Projet non trouvé");
            return;
        }

        try {
            List<Task> tasks = taskService.getAllTasks();
            if (tasks.isEmpty()) {
                System.out.println("ℹ️ Aucune tâche pour ce projet");
                return;
            }

            System.out.println("\n✅ Tâches du projet " + name + ":");
            tasks.forEach(t -> System.out.printf("- %s (Échéance: %s, Statut: %s)\n",
                    t.getName(), t.getDueDate(), t.getStatus()));
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    // ==================== GESTION TÂCHES ====================
    private static void manageTasks() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- GESTION DES TÂCHES ---");
            System.out.println("1. 🆕 Créer une tâche");
            System.out.println("2. 📜 Lister toutes les tâches");
            System.out.println("3. 🔍 Rechercher une tâche");
            System.out.println("4. ✏️ Modifier une tâche");
            System.out.println("5. 🗑️ Supprimer une tâche");
            System.out.println("6. 🏷️ Filtrer par statut");
            System.out.println("0. ↩️ Retour");

            int choice = safeReadInt("Choix: ");
            switch (choice) {
                case 1 -> createTask();
                case 2 -> listAllTasks();
                case 3 -> findTaskByName();
                case 4 -> updateTask();
                case 5 -> deleteTask();
                case 6 -> filterTasksByStatus();
                case 0 -> back = true;
                default -> System.out.println("❌ Option invalide!");
            }
        }
    }

    private static void createTask() {
        try {
            System.out.print("Nom du projet associé: ");
            String projectName = scanner.nextLine();

            Project project = projectService.findByName(projectName);
            if (project == null) {
                System.out.println("❌ Projet non trouvé");
                return;
            }

            System.out.print("Nom de la tâche: ");
            String name = scanner.nextLine();

            System.out.print("Description: ");
            String description = scanner.nextLine();

            LocalDate dueDate = readDate("Date d'échéance (AAAA-MM-JJ): ");
            
            System.out.print("Priorité (LOW/MEDIUM/HIGH): ");
            TaskPriority priority = TaskPriority.valueOf(scanner.nextLine().toUpperCase());

            Task task = taskService.createTask(name, description, dueDate, TaskStatus.TODO, priority);
            taskService.addTaskToProject(task, projectName);
            System.out.println("✅ Tâche créée avec succès");
        } catch (DateTimeParseException e) {
            System.err.println("❌ Format de date invalide");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Valeur invalide: " + e.getMessage());
        } catch (BusinessException | SQLException | DuplicateTaskException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void listAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            if (tasks.isEmpty()) {
                System.out.println("ℹ️ Aucune tâche trouvée");
                return;
            }

            System.out.println("\n📋 Liste des tâches:");
            tasks.forEach(t -> System.out.printf("- %s (Projet: %s, Échéance: %s, Statut: %s)\n",
                    t.getName(), getProjectForTask(t), t.getDueDate(), t.getStatus()));
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static String getProjectForTask(Task task) {
        // Implémentation simplifiée - à adapter selon votre modèle de données
        try {
            List<Project> projects = projectService.getAllProjects();
            for (Project p : projects) {
                if (p.getTasks().contains(task)) {
                    return p.getName();
                }
            }
        } catch (SQLException e) {
            return "Inconnu";
        }
        return "Sans projet";
    }

    private static void findTaskByName() {
        System.out.print("Nom de la tâche: ");
        String name = scanner.nextLine();

        try {
            Task task = taskService.findByName(name);
            if (task != null) {
                displayTaskDetails(task);
            } else {
                System.out.println("❌ Tâche non trouvée");
            }
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void displayTaskDetails(Task task) {
        System.out.println("\n📋 Détails de la tâche:");
        System.out.println("Nom: " + task.getName());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Date échéance: " + task.getDueDate());
        System.out.println("Statut: " + task.getStatus());
        System.out.println("Priorité: " + task.getPriority());
    }

    private static void updateTask() {
        System.out.print("Nom de la tâche à modifier: ");
        String name = scanner.nextLine();

        try {
            Task task = taskService.findByName(name);
            if (task == null) {
                System.out.println("❌ Tâche non trouvée");
                return;
            }

            System.out.print("Nouvelle description (laisser vide pour ne pas modifier): ");
            String newDesc = scanner.nextLine();
            
            System.out.print("Nouveau statut (TODO/IN_PROGRESS/DONE) (laisser vide pour ne pas modifier): ");
            String newStatus = scanner.nextLine();

            System.out.print("Nouvelle date d'échéance (AAAA-MM-JJ) (laisser vide pour ne pas modifier): ");
            String newDueDate = scanner.nextLine();

            if (!newDesc.isEmpty()) task.setDescription(newDesc);
            if (!newStatus.isEmpty()) task.setStatus(TaskStatus.valueOf(newStatus));
            if (!newDueDate.isEmpty()) task.setDueDate(LocalDate.parse(newDueDate));

            boolean success = taskService.updateTask(task);
            System.out.println(success ? "✅ Tâche mise à jour" : "❌ Échec de la mise à jour");
        } catch (BusinessException | DateTimeParseException | IllegalArgumentException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void deleteTask() {
        System.out.print("Nom de la tâche à supprimer: ");
        String name = scanner.nextLine();

        try {
            Task task = taskService.findByName(name);
            if (task == null) {
                System.out.println("❌ Tâche non trouvée");
                return;
            }

            System.out.print("Confirmer la suppression (o/n)? ");
            if (scanner.nextLine().equalsIgnoreCase("o")) {
                boolean success = taskService.deleteTask(task);
                System.out.println(success ? "✅ Tâche supprimée" : "❌ Échec de la suppression");
            }
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void filterTasksByStatus() {
        System.out.print("Statut à filtrer (TODO/IN_PROGRESS/DONE): ");
        String status = scanner.nextLine().toUpperCase();

        try {
            List<Task> tasks = taskService.getTasksByStatus(TaskStatus.valueOf(status));
            if (tasks.isEmpty()) {
                System.out.println("ℹ️ Aucune tâche avec ce statut");
                return;
            }

            System.out.println("\n📋 Tâches avec statut " + status + ":");
            tasks.forEach(t -> System.out.printf("- %s (Projet: %s, Échéance: %s)\n",
                    t.getName(), getProjectForTask(t), t.getDueDate()));
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Statut invalide");
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    // ==================== GESTION RAPPELS ====================
    private static void manageReminders() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- GESTION DES RAPPELS ---");
            System.out.println("État actuel: " + (remindersActive ? "🟢 ACTIF" : "🔴 INACTIF"));
            System.out.println("1. " + (remindersActive ? "Désactiver" : "Activer") + " les rappels");
            System.out.println("2. Voir les tâches en retard");
            System.out.println("0. ↩️ Retour");

            int choice = safeReadInt("Choix: ");
            switch (choice) {
                case 1 -> toggleReminders();
                case 2 -> showOverdueTasks();
                case 0 -> back = true;
                default -> System.out.println("❌ Option invalide!");
            }
        }
    }

    private static void toggleReminders() {
        remindersActive = !remindersActive;
        if (remindersActive) {
            reminderService.start();
            System.out.println("🔔 Rappels activés - Vérification toutes les minutes");
        } else {
            reminderService.stop();
            System.out.println("🔕 Rappels désactivés");
        }
    }

    private static void showOverdueTasks() {
        List<Task> overdueTasks = taskService.getAllTasks().stream()
                .filter(t -> t.getDueDate().isBefore(LocalDate.now()))
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .toList();

        if (overdueTasks.isEmpty()) {
            System.out.println("✅ Aucune tâche en retard");
            return;
        }

        System.out.println("\n⚠️ Tâches en retard:");
        overdueTasks.forEach(t -> System.out.printf("- %s (Projet: %s, Échéance: %s)\n",
                t.getName(), getProjectForTask(t), t.getDueDate()));
    }

    // ==================== GENERATION RAPPORTS ====================
    private static void generateReports() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- RAPPORTS ---");
            System.out.println("1. 📈 Progression globale");
            System.out.println("2. 📋 Détails par projet");
            System.out.println("3. ✅ Tâches complétées");
            System.out.println("4. ⏳ Tâches en cours");
            System.out.println("5. 💾 Exporter les données");
            System.out.println("0. ↩️ Retour");

            int choice = safeReadInt("Choix: ");
            switch (choice) {
                case 1 -> generateGlobalReport();
                case 2 -> generateProjectReport();
                case 3 -> generateCompletedTasksReport();
                case 4 -> generateInProgressTasksReport();
                case 5 -> exportData();
                case 0 -> back = true;
                default -> System.out.println("❌ Option invalide!");
            }
        }
    }

    private static void generateGlobalReport() throws SQLException {
        List<Project> projects = projectService.getAllProjects();
        long totalProjects = projects.size();
        long completedProjects = projects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.DONE)
                .count();
        
        List<Task> tasks = taskService.getAllTasks();
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();

        System.out.println("\n📊 RAPPORT GLOBAL");
        System.out.println("Projets: " + completedProjects + "/" + totalProjects + " complétés");
        System.out.println("Tâches: " + completedTasks + "/" + totalTasks + " complétées");
        System.out.printf("Progression moyenne: %.1f%%\n",
                projects.stream().mapToDouble(Project::getProgression).average().orElse(0));
    }

    private static void generateProjectReport() throws SQLException {
        listAllProjects(true);
        System.out.print("\nNom du projet pour le détail (laisser vide pour annuler): ");
        String name = scanner.nextLine();

        if (name.isEmpty()) return;

        Project project = projectService.findByName(name);
        if (project == null) {
            System.out.println("❌ Projet non trouvé");
            return;
        }

        System.out.println("\n📋 RAPPORT PROJET: " + name);
        System.out.println("Statut: " + project.getStatus());
        System.out.printf("Progression: %.1f%%\n", project.getProgression());
        System.out.println("Date début: " + project.getFrom());
        System.out.println("Date fin: " + (project.getTo() != null ? project.getTo() : "Non définie"));

        try {
            List<Task> tasks = taskService.getAllTasks();
            System.out.println("\nTâches associées (" + tasks.size() + "):");
            
            long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
            System.out.printf("✅ Complétées: %d (%.1f%%)\n", doneCount, 
                    tasks.isEmpty() ? 0 : (double) doneCount / tasks.size() * 100);
            
            System.out.println("\nDétail par statut:");
            for (TaskStatus status : TaskStatus.values()) {
                long count = tasks.stream().filter(t -> t.getStatus() == status).count();
                System.out.printf("- %s: %d\n", status, count);
            }
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void generateCompletedTasksReport() {
        try {
            List<Task> tasks = taskService.getTasksByStatus(TaskStatus.DONE);
            if (tasks.isEmpty()) {
                System.out.println("ℹ️ Aucune tâche complétée");
                return;
            }

            System.out.println("\n✅ TÂCHES COMPLÉTÉES (" + tasks.size() + ")");
            tasks.forEach(t -> System.out.printf("- %s (Projet: %s, Complétée le: %s)\n",
                    t.getName(), getProjectForTask(t), LocalDate.now())); // Date fictive
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void generateInProgressTasksReport() {
        try {
            List<Task> tasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS);
            if (tasks.isEmpty()) {
                System.out.println("ℹ️ Aucune tâche en cours");
                return;
            }

            System.out.println("\n⏳ TÂCHES EN COURS (" + tasks.size() + ")");
            tasks.forEach(t -> {
                String dueInfo = t.getDueDate().isBefore(LocalDate.now()) 
                        ? "⚠️ EN RETARD (depuis " + LocalDate.now().until(t.getDueDate()).getDays() + " jours)"
                        : "Échéance: " + t.getDueDate();
                System.out.printf("- %s (Projet: %s, %s)\n",
                        t.getName(), getProjectForTask(t), dueInfo);
            });
        } catch (BusinessException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void exportData() {
        try {
            List<Project> projects = projectService.getAllProjects();
            FileManagement fileManager = new FileManagement(projects);
            fileManager.writeToFile();
            System.out.println("✅ Données exportées avec succès");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'export: " + e.getMessage());
        }
    }

    // ==================== UTILITAIRES ====================
    private static int safeReadInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("❌ Format de date invalide (AAAA-MM-JJ requis)");
            }
        }
    }

    private static boolean confirmExit() {
        System.out.print("Confirmer la sortie (o/n)? ");
        return scanner.nextLine().equalsIgnoreCase("o");
    }
}