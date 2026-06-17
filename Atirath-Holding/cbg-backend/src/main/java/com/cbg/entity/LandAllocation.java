package com.cbg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "land_allocation")
@Getter
@Setter
public class LandAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "land_id")
    private Long landId;

    @Column(name = "coy_id")
    private Long coyId;

    @Column(name = "plt_id")
    private Long pltId;

    @Column(name = "survey_no", length = 30)
    private String surveyNo;

    @Column(name = "land_code", length = 5)
    private String landCode;

    @Column(name = "land_name")
    private String landName;

    @Column(name = "land_owner")
    private String landOwner;

    @Column(name = "contact", length = 10)
    private String contact;

    @Column(name = "land_size")
    private Double landSize;

    @Column(name = "location")
    private String location;

    @Column(name = "st_id")
    private Long stId;

    @Column(name = "district", length = 30)
    private String district;

    @Column(name = "pincode", length = 6)
    private String pincode;

    @Column(name = "village_name", length = 50)
    private String villageName;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "allocation_type")
    private String allocationType;

    @Column(name = "remarks")
    private String remarks;
}
