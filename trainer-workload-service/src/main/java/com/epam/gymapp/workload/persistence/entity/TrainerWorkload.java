package com.epam.gymapp.workload.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "trainer_workload",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"username", "year_value", "month_value"}
        )
)
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "year_value", nullable = false)
    private Integer year;

    @Column(name = "month_value", nullable = false)
    private Integer month;

    @Column(name = "duration", nullable = false)
    private Long trainingSummaryDuration;
}
