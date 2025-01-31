package com.usecase.controller;

import java.util.HashMap;
import java.util.Map;

public class Test {
	public static int maxProfit(int[] prices) {
		// Initialize variables
		int minPrice = Integer.MAX_VALUE;
		int maxProfit = 0;

		// Iterate through the array
		for (int price : prices) {
			// Update the minimum price if the current price is lower
			if (price < minPrice) {
				minPrice = price;
			}
			// Calculate the potential profit and update maxProfit if it's higher
			else if (price - minPrice > maxProfit) {
				maxProfit = price - minPrice;
			}
		}

		return maxProfit;
	}

	public static void main(String[] args) {
		
		/*
		int[] prices1 = { 7, 1, 5, 3, 6, 4 };
		System.out.println("Max Profit: " + maxProfit(prices1)); // Output: 5

		int[] prices2 = { 7, 6, 4, 3, 1 };
		System.out.println("Max Profit: " + maxProfit(prices2)); // Output: 0

		System.out.println(getNextChars("AbhKihfZhI"));
		
		char result = firstUniqueChar("swwiiss");

        if (result != '\0') {
            System.out.println("First unique character: " + result);
        } else {
            System.out.println("No unique character found.");
        }
        */
	}

	public static String getNextChars(String input) {
		StringBuilder output = new StringBuilder();

		for (char ch : input.toCharArray()) {
			if (ch >= 'a' && ch <= 'z') {
				output.append(ch == 'z' ? 'a' : (char) (ch + 1));
			} else if (ch >= 'A' && ch <= 'Z') {
				output.append(ch == 'Z' ? 'A' : (char) (ch + 1));
			} else {
				output.append(ch);
			}
		}
		return output.toString();
	}
	
	public static char firstUniqueChar(String input) {

        Map<Character, Integer> frequencyMap = new HashMap<>();
        
        for (char ch : input.toCharArray()) {
            frequencyMap.put(ch, frequencyMap.getOrDefault(ch, 0) + 1);
        }
        for (char ch : input.toCharArray()) {
            if (frequencyMap.get(ch) == 1) {
                return ch;
            }
        }
        return '\0';
    }

}
