package com.usecase.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class TestHcl {
	public static void main(String[] args) {

		List<String> employees = new ArrayList<>();
		employees.add("Asdf");
		employees.add("lkih");
		employees.add("Oujk");
		employees.add("Jnjk");

		employees.add("Oujk");
		employees.add("Jnjk");

		List<String> sortedList = employees.stream().sorted().collect(Collectors.toList());
		sortedList.forEach(emp -> System.out.println(emp));
		// String str = employees.stream().
		Object a = new Object();
		Object b = a;
		Object c = b;
		a = c;

		sortedSquares(new int[] { -4, -1, 0, 3, 10 });
		getCount(employees);
	}

	public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
		PriorityQueue<ListNode> minHeap = new PriorityQueue<ListNode>((a, b) -> a.val - b.val);
		while (list1 != null) {
			minHeap.add(list1);
			list1 = list1.next;
		}
		while (list2 != null) {
			minHeap.add(list2);
			list2 = list2.next;
		}

		ListNode dummy = new ListNode(-1);
		ListNode current = dummy;
		while (!minHeap.isEmpty()) {
			current.next = minHeap.poll();
			current = current.next;
		}
		current.next = null;
		return dummy;
	}

	public static int[] sortedSquares(int[] nums) {
		int n = nums.length;
		int[] sqList = new int[n];
		int left = 0, right = n - 1;
		int index = n - 1;

		while (left <= right) {
			int leftSq = nums[left] * nums[left];
			int rightSq = nums[right] * nums[right];

			if (leftSq > rightSq) {
				sqList[index--] = leftSq;
				left++;
			} else {
				sqList[index--] = rightSq;
				right--;
			}
		}

		// Arrays.sort(sqList);
		return sqList;
	}

	public static void getCount(List<String> input) {
		Map<String, Integer> map = input.stream().collect(Collectors.toMap(st -> st, v -> 1, Integer::sum));
		System.out.println(map);

		String s1 = "Java";
		String s2 = new String("Java");
		System.out.println(s1 == s2);
		System.out.println(s1.equals(s2));
	}
}
