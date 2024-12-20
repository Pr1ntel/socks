package org.example.socks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;

@Entity
@Table(name = "socks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Socks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color")
    private String color;

    @Column(name = "percentage_of_cotton_content")
    private int percentageOfCottonContent;

    @Column(name = "quantity")
    private int quantity;
}