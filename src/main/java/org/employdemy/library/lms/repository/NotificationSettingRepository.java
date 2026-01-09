package org.employdemy.library.lms.repository;

import org.employdemy.library.lms.model.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSettings,Long > {


}
