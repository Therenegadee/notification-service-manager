package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.Placeholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceholderRepository extends JpaRepository<Placeholder, Integer> {
}
