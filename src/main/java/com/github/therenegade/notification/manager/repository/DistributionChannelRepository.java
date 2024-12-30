package com.github.therenegade.notification.manager.repository;

import com.github.therenegade.notification.manager.entity.DistributionChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionChannelRepository extends JpaRepository<DistributionChannel, Integer> {
}
