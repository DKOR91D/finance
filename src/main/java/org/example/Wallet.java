package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    public List<Operation> incomes = new ArrayList<>();
    public List<Operation> expenses = new ArrayList<>();
    public Map<String, Double> categoriesBudget = new HashMap<>();
}
