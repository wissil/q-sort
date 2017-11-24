package filip.custom.algorithms.sorting;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		final int SIZE = 150_000;
		Random rand = new Random();
		Integer[] data = new Integer[SIZE];
		
		for(int i = 0; i < data.length; i++) {
			data[i] = rand.nextInt();
		}
				
		
		long t0 = System.currentTimeMillis();
		QSort.sort(data);
		long t1 = System.currentTimeMillis();
		
		System.out.println("Sorted: " + QSort.isSorted(data));
		System.out.println("Time elapsed: " + (t1-t0) + " ms");
	}

}
