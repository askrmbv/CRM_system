package repository.interfaces;

import models.ActivityLog;
import java.util.List;

public interface IActivityLogRepository {
    boolean log(int userId, String actionType, String description);
    List<ActivityLog> getAll();
    List<ActivityLog> getRecent(int limit);

    // Lambda methods
    List<ActivityLog> getLogsByActionType(String actionType);
    List<ActivityLog> getLogsByUserId(int userId);
    long countActionsByType(String actionType);
    List<String> getAllActionTypes();
}