package com.example.sena_bhawan.projection;

// Create a projection interface
public interface AgeBandProjection {
    Long getUnder30();
    Long getAge31to35();
    Long getAge36to40();
    Long getAge41to45();
    Long getAge46to50();
    Long getOver50();
}