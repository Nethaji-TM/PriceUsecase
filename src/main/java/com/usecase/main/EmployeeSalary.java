package com.usecase.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EmployeeSalary {
    private static Map<Integer, List<Integer>> hierarchy = new HashMap<>();
    private static Map<Integer, Integer> salaryMap = new HashMap<>();

    public static void main(String[] args) {
        // Employee data (empId, parentId, salary)
        int[][] employees = {
            {1, 0, 200000},
            {2, 1, 100000},
            {3, 1, 120000},
            {4, 2, 50000},
            {5, 2, 60000},
            {6, 3, 55000},
            {7, 3, 65000}
        };

        // Build the hierarchy map and salary map
        for (int[] emp : employees) {
            int empId = emp[0], parentId = emp[1], salary = emp[2];
            salaryMap.put(empId, salary);
            hierarchy.putIfAbsent(parentId, new ArrayList<>());
            hierarchy.get(parentId).add(empId);
        }

        // Compute the total salary for each manager
        for (int empId : salaryMap.keySet()) {
            int totalSalary = computeTotalSalary(empId);
            System.out.println("Total salary for Manager " + empId + " and subordinates: " + totalSalary);
        }
    }

    private static int computeTotalSalary(int managerId) {
        int totalSalary = salaryMap.getOrDefault(managerId, 0);
        if (hierarchy.containsKey(managerId)) {
            for (int subordinate : hierarchy.get(managerId)) {
                totalSalary += computeTotalSalary(subordinate);
            }
        }
        return totalSalary;
    }
}
