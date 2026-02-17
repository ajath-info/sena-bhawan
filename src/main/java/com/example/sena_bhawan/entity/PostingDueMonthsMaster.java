package com.example.sena_bhawan.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "posting_due_months_master",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "months")
        }
)
@Getter
@Setter
public class PostingDueMonthsMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "months", nullable = false)
    private Integer months;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
