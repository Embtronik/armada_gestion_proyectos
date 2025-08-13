// EsignCodeRepository.java
package com.example.e_sign.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.e_sign.entity.EsignCode;

import jakarta.persistence.LockModeType;

public interface EsignCodeRepository extends JpaRepository<EsignCode, UUID> {

  @Query("""
    select e from EsignCode e
     where e.userId = :userId
       and e.code = :code
       and e.usedAt is null
       and e.expiresAt > :now
     """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<EsignCode> findActiveForVerify(@Param("userId") String userId,
                                          @Param("code") String code,
                                          @Param("now") OffsetDateTime now);
  
  @Query("""
    select c from EsignCode c
    where c.userId = :userId
     and (:docId is null or c.documentId = :docId)
     and c.expiresAt > :now
     and c.usedAt is null
    """)
  List<EsignCode> findActiveCodes(@Param("userId") String userId,
                                @Param("docId") UUID docId,
                                @Param("now") OffsetDateTime now);

}
