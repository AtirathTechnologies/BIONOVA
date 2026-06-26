package com.bionova.repository;

import com.bionova.entity.MilestoneLive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface MilestoneLiveRepository extends JpaRepository<MilestoneLive, Long> {
    List<MilestoneLive> findByPrjId(Long prjId);
    boolean existsByMlstnCd(String mlstnCd);

    @Query("SELECT COUNT(m) > 0 FROM MilestoneLive m WHERE m.mlstnCd = :mlstnCd AND m.mId <> :mId")
    boolean existsByMlstnCdAndMIdNot(@Param("mlstnCd") String mlstnCd, @Param("mId") Long mId);
}
