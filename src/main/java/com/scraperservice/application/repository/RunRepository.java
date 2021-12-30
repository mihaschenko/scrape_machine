package com.scraperservice.application.repository;

import com.scraperservice.application.entity.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRepository extends JpaRepository<Run, Integer> {
    @Query("select r from Run r where r.hash = :hash")
    Run findByHash(@Param("hash") String hash);
}
