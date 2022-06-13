package com.scraperservice.web.repository;

import com.scraperservice.web.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ConfigRepository extends JpaRepository<Config, Integer> {
}
