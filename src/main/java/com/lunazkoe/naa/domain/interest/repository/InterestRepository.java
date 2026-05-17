package com.lunazkoe.naa.domain.interest.repository;

import com.lunazkoe.naa.domain.interest.entity.Interest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, UUID> {

}
