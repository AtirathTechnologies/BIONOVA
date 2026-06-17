package com.cbg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plant_master")
@Getter
@Setter
public class PlantMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plt_id")
    private Long pltId;

    @Column(name = "plt_cd")
    private String pltCd;

    @Column(name = "coy_id")
    private Long coyId;

    @Column(name = "plt_nm", nullable = false)
    private String pltNm;

    @Column(name = "cap")
    private Double cap;

    @Column(name = "email")
    private String email;

    @Column(name = "addr")
    private String addr;

    @Column(name = "dist")
    private String dist;

    @Column(name = "st_id")
    private Long stId;

    @Column(name = "pin")
    private String pin;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location")
    private String location;

    @Column(name = "addl_rem")
    private String addlRem;
}