package com.example.e_sign.repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.e_sign.entity.CodeRequestStatus;
import com.example.e_sign.entity.EsignCodeRequest;

public interface EsignCodeRequestRepository extends JpaRepository<EsignCodeRequest, UUID> {

  Optional<EsignCodeRequest> findByEsignCodeId(UUID esignCodeId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update EsignCodeRequest r
         set r.status = :status,
             r.processedAt = :now
       where r.esignCodeId = :codeId
      """)
  int updateStatusByEsignCodeId(@Param("codeId") UUID codeId,
                                @Param("status") CodeRequestStatus status,
                                @Param("now") OffsetDateTime now);
}
